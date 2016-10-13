package refstore.messaging;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMqBackedMessenger implements Messenger {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqBackedMessenger.class);

	private class PublisherContext implements AutoCloseable {

		private final Connection connection;
		private final Channel channel;

		public PublisherContext(ConnectionFactory factory) throws IOException, TimeoutException {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.confirmSelect();
		}

		@Override
		public void close() throws IOException, TimeoutException {
			IOException exception = null;
			try {
				channel.close();
			} catch (IOException e) {
				log.error("Error closing publisher channel: '{}'", e.getMessage());
				exception = e;
			}
			try {
				connection.close();
			} catch (IOException e) {
				log.error("Error closing publisher connection: '{}'", e.getMessage());
				exception = e;
			}
			if (exception != null) {
				throw exception;
			}
		}

	}

	private class ConsumerContext implements AutoCloseable {

		private final Connection connection;
		private final Channel channel;
		private final List<ReceiverContext> receiverContexts = new LinkedList<>();

		public ConsumerContext(ConnectionFactory factory) throws IOException, TimeoutException {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare("broadcast", false, false, true, null);
		}

		@Override
		public void close() throws Exception {
			Exception exception = null;
			for (ReceiverContext receiverContext : receiverContexts) {
				try {
					receiverContext.close();
				} catch (IOException e) {
					log.error("Error closing receiver context: '{}'", e.getMessage());
					if (exception == null) {
						exception = e;
					}
				}
			}
			try {
				channel.close();
			} catch (IOException | TimeoutException e) {
				log.error("Error closing consumer channel");
				if (exception == null) {
					exception = e;
				}
			}
			try {
				connection.close();
			} catch (IOException e) {
				log.error("Error closing consumer connection");
				if (exception == null) {
					exception = e;
				}
			}
			if (exception != null) {
				throw exception;
			}
		}
	}

	private class ReceiverContext implements AutoCloseable {
		private final Receiver receiver;

		private final List<Channel> channels = new LinkedList<>();

		public ReceiverContext(Receiver receiver) {
			this.receiver = receiver;
		}

		public void addQueue(String exchange, String queue) {
			try {
				final Channel channel = consumer.connection.createChannel();
				channel.basicQos(1);
				channel.queueBind(queue, exchange, queue);
				channel.basicConsume(queue, new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
						String message = new String(body, "UTF-8");
						channel.basicAck(envelope.getDeliveryTag(), false);
						if (!receiver.receive(message)) {
							synchronized (channels) {
								channels.remove(channel);
							}
							try {
								channel.close();
							} catch (TimeoutException e) {
								log.warn("Timed out waiting for channel to close: '{}'", e.getMessage());
							}
						}
					}
				});
				synchronized (channels) {
					channels.add(channel);
				}
			} catch (IOException e) {
				log.error("Error adding queue '{}': '{}'", queue, e.getMessage());
			}
		}

		@Override
		public void close() throws Exception {
			Exception exception = null;
			synchronized (channels) {
				for (Channel channel : channels) {
					try {
						channel.close();
					} catch (IOException | TimeoutException e) {
						log.error("Error closing channel: '{}'", e.getMessage());
						if (exception == null) {
							exception = e;
						}
					}
				}
				channels.clear();
			}
			if (exception != null) {
				throw exception;
			}
		}

	}

	private final PublisherContext publisher;
	private final ConsumerContext consumer;

	public RabbitMqBackedMessenger(ConnectionFactory factory) throws IOException, TimeoutException {
		publisher = new PublisherContext(factory);
		consumer  = new ConsumerContext(factory);
	}

	@Override
	public void send(String queue, String message) {
		try {
			publisher.channel.basicPublish("amq.direct", queue, null, message.getBytes("UTF-8"));
		} catch (IOException e) {
			log.error("Error sending message to queue '{}': '{}'", queue, e.getMessage());
		}
	}

	@Override
	public void broadcast(String message) {
		try {
			publisher.channel.basicPublish("amq.fanout", "", null, message.getBytes("UTF-8"));
		} catch (IOException e) {
			log.error("Error broadcasting message: '{}'", e.getMessage());
		}
	}

	@Override
	public synchronized void add(Receiver receiver) {
		ReceiverContext context = new ReceiverContext(receiver);
		context.addQueue("amq.fanout", "broadcast");
		for (String queue : receiver.getQueues()) {
			context.addQueue("amq.direct", queue);
		}
		consumer.receiverContexts.add(context);
	}

	@Override
	public void createQueue(String queue) {
		this.createQueue(queue, false);
	}

	@Override
	public void createQueue(String queue, boolean exclusive) {
		try {
			consumer.channel.queueDeclare(queue, false, exclusive, true, null);
		} catch (IOException e) {
			log.error("Error creating queue '{}': '{}'", queue, e.getMessage());
		}
	}

	@Override
	public void close() {
		try {
			publisher.close();
		} catch (IOException | TimeoutException e) {
			log.error("Error closing publisher context: '{}'", e.getMessage());
		}

		try {
			consumer.close();
		} catch (Exception e) {
			log.error("Error closing consumer context: '{}'", e.getMessage());
		}
	}
}
