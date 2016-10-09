package refstore.messaging;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// TODO: Implement auto-recovery connection and act accordingly
public class RabbitMqBasedMessenger implements Messenger {

	private class ReceiverContext {
		Receiver receiver;
		Channel sharedWork;
		Channel exclusiveWork;
		
		public ReceiverContext(Receiver receiver) {
			this.receiver = receiver;
		}
	}
	
	private static final Logger log = LoggerFactory.getLogger(RabbitMqBasedMessenger.class);

	private final Map<Class<? extends Receiver>, List<ReceiverContext>> contexts = new HashMap<>();

	private Connection publisherConnection;
	private Channel publisherChannel;

	private Connection consumerConnection;

	public RabbitMqBasedMessenger(ConnectionFactory factory) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, IOException, TimeoutException {
		try {
			publisherConnection = factory.newConnection();
			publisherChannel = publisherConnection.createChannel();
			publisherChannel.confirmSelect();
			consumerConnection = factory.newConnection();
		} catch (IOException | TimeoutException e) {
			log.warn("Error while creating messenger: '{}'", e.getMessage());
			try {
				close();
			} catch (IOException e1) {
				log.warn("Error while trying to close messenger on failed construction: '{}'", e.getMessage());
			}
			throw e;
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (publisherChannel != null) {
				publisherChannel.close();
			}
		} catch (TimeoutException e) {
			log.warn("Timed out while waiting for publisher channel to close: {}", e.getMessage());
		}

		for (Entry<Class<? extends Receiver>, List<ReceiverContext>> entry : contexts.entrySet()) {
			for (ReceiverContext context : entry.getValue()) {
				try {
					context.exclusiveWork.close();
					context.sharedWork.close();
				} catch (TimeoutException e) {
					log.warn("Timed out while waiting for a consumer channel to close: {}", e.getMessage());
				}
			}
		}

		if (publisherConnection != null) {
			publisherConnection.close();	
		}

		if (consumerConnection != null) {
			consumerConnection.close();
		}
	}

	@Override
	public String getNodeId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void ping(Receiver replyTo) {
		broadcast("PING", replyTo);
	}
	
	@Override
	public void ping(Class<? extends Receiver> receiverType, Receiver replyTo) {
		broadcast(receiverType, "PING", replyTo);
	}

	@Override
	public void broadcast(String message, Receiver replyTo) {
		try {
			send(Receiver.class.getCanonicalName(), message, replyTo);
		} catch (IOException e) {
			log.warn("Error broadcasting message to all receivers: '{}'", e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("Failed when sending message:\n{}", message);
			}
		}
	}

	@Override
	public void broadcast(Class<? extends Receiver> receiverType, String message, Receiver replyTo) {
		try {
			send(receiverType.getCanonicalName(), message, replyTo);
		} catch (IOException e) {
			log.warn("Error broadcasting message to receivers of type '{}': '{}'", receiverType.getCanonicalName(), e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("Failed when sending message:\n{}", message);
			}
		}
	}

	@Override
	public void send(Class<? extends Receiver> receiverType, String message, Receiver replyTo) {
		String routingKey = receiverType.getCanonicalName();
		try {
			send(routingKey, message, replyTo);
		} catch (IOException e) {
			log.warn("Error sending direct message to receiver at '{}': '{}'", routingKey, e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("Failed when sending message:\n{}", message);
			}
		}
	}

	private void send(String routingKey, String message, Receiver replyTo) throws IOException {
		String replyToString = String.format("%s:%s", getNodeId(), replyTo.getReceiverId());
		BasicProperties props = new BasicProperties()
				.builder()
				.replyTo(replyToString)
				.build();
		
		publisherChannel.basicPublish("amq.direct", routingKey, props, message.getBytes("UTF-8"));
	}

	@Override
	public synchronized void add(final Receiver receiver) {
		if (!contexts.containsKey(receiver.getClass())) {
			contexts.put(receiver.getClass(), new LinkedList<>());
		}
		ReceiverContext context = new ReceiverContext(receiver);
		contexts.get(receiver.getClass()).add(context);
		
		String directRoutingKey = String.format("%s-%s", receiver.getClass().getCanonicalName(), receivers.get(receiver.getClass()).size());
		String broadcastByTypeRoutingKey = receiver.getClass().getCanonicalName();
		String broadcastRoutingKey = Receiver.class.getCanonicalName();
		String queue = directRoutingKey;

		try {
			String workerQueue = getClass().getCanonicalName();
			Channel workerChannel = consumerConnection.createChannel();
			workerChannel.queueDeclare(workerQueue, false, false, true, null);
			workerChannel.queueBind(workerQueue, "amq.direct", workerQueue);
			workerChannel.basicQos(1);
			workerChannel.basicConsume(workerQueue, new DefaultConsumer(workerChannel) {
				
			});
			
			Channel directChannel = consumerConnection.createChannel();
			directChannel.queueDeclare(queue, false, true, true, null);
			directChannel.queueBind(queue, "amq.direct", directRoutingKey);
			directChannel.queueBind(queue, "amq.direct", broadcastByTypeRoutingKey);
			directChannel.queueBind(queue, "amq.direct", broadcastRoutingKey);
			directChannel.basicQos(100);
			directChannel.basicConsume(queue, new DefaultConsumer(directChannel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					Receiver replyTo = null;
					if (properties != null && properties.getReplyTo() != null) {
						String[] parts = properties.getReplyTo().split(":");
						if (parts.length == 2) {
							replyTo = new Receiver(parts[0], parts[1]);
						} else {
							log.warn("Reply to did not have expected format: '{}'", properties.getReplyTo());
							return;
						}
					}
					if ("PING".equals(message)) {
						directChannel.basicAck(envelope.getDeliveryTag(), false);
					} else {
						boolean keepGoing = receiver.receive(message, replyTo);
						directChannel.basicAck(envelope.getDeliveryTag(), false);
						if (!keepGoing) {
							stopAndRemove(receiver);
						}
					}
				}
			});
			if (!channels.containsKey(receiver.getClass())) {
				channels.put(receiver.getClass(), new LinkedList<>());
			}
			channels.get(receiver.getClass()).add(workerChannel);
			channels.get(receiver.getClass()).add(directChannel);
		} catch (IOException e) {
			log.warn("Error adding receiver: {}", e.getMessage());
		}
	}

	private synchronized void stopAndRemove(Receiver receiver) {
		if (receivers.containsKey(receiver.getClass())) {
			int index = 0;
			for (Receiver currentReceiver : receivers.get(receiver.getClass())) {
				if (currentReceiver == receiver) {
					break;
				} else {
					index++;
				}
			}

			if (index == receivers.get(receiver.getClass()).size()) {
				log.warn("Could not find the receiver to be stopped");
				return;
			}

			Channel channel = channels.get(receiver.getClass()).get(index);			
			try {
				channel.close();
				channels.get(receiver.getClass()).remove(index);
				receivers.get(receiver.getClass()).remove(index);

				if (receivers.get(receiver.getClass()).isEmpty()) {
					indexes.remove(receiver.getClass());
				} else {
					indexes.put(receiver.getClass(), indexes.get(receiver.getClass()) % receivers.get(receiver.getClass()).size());
				}
			} catch (IOException e) {
				log.warn("Error when trying to close channel: '{}'", e.getMessage());
			} catch (TimeoutException e) {
				log.warn("Timed out while waiting for channel to close: '{}'", e.getMessage());
			}
		}
	}
}
