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

public class RabbitMqBasedMessenger implements Messenger {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqBasedMessenger.class);

	private final Map<Class<? extends Receiver>, List<Receiver>> receivers = new HashMap<>();
	private final Map<Class<? extends Receiver>, List<Channel>> channels = new HashMap<>();
	private final Map<Class<? extends Receiver>, Integer> indexes = new HashMap<>();

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
		
		for (Entry<Class<? extends Receiver>, List<Channel>> entry : channels.entrySet()) {
			for (Channel channel : entry.getValue()) {
				try {
					channel.close();
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
	public void broadcast(Class<? extends Receiver> receiverType, String message) {
		send(receiverType, receiverType.getCanonicalName(), message);
	}

	@Override
	public void send(Class<? extends Receiver> receiverType, String message) {
		String routingKey = String.format("%s-%d", receiverType.getCanonicalName(), getAndUpdateIndex(receiverType));
		send(receiverType, routingKey, message);
	}

	private void send(Class<? extends Receiver> receiverType, String routingKey, String message) {
		if (receivers.containsKey(receiverType)) {
			try {
				publisherChannel.basicPublish("amq.direct", routingKey, null, message.getBytes("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.warn("Receiver not registered: '{}'", receiverType);
		}
		
	}
	
	private synchronized int getAndUpdateIndex(Class<? extends Receiver> receiverType) {
		int result = indexes.containsKey(receiverType) ? indexes.get(receiverType) : 0;
		indexes.put(receiverType, (result + 1) % receivers.get(receiverType).size());
		return result;
	}
	
	@Override
	public synchronized void add(final Receiver receiver) {
		if (!receivers.containsKey(receiver.getClass())) {
			receivers.put(receiver.getClass(), new LinkedList<>());
		}
		receivers.get(receiver.getClass()).add(receiver);

		String queue = String.format("%s", receiver.getClass().getCanonicalName());
		String directRoutingKey = String.format("%s-%d", queue, receivers.get(receiver.getClass()).size()-1);
		String broadcastRoutingKey = queue;
		
		try {
			Channel channel = consumerConnection.createChannel();
			channel.queueDeclare(queue, false, true, true, null);
			channel.queueBind(queue, "amq.direct", directRoutingKey);
			channel.queueBind(queue, "amq.direct", broadcastRoutingKey);
			channel.basicQos(100);
			channel.basicConsume(queue, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					boolean keepGoing = receiver.receive(new String(body, "UTF-8"));
					channel.basicAck(envelope.getDeliveryTag(), false);
					if (!keepGoing) {
						stopAndRemove(receiver);
					}
					
				}
			});
			if (!channels.containsKey(receiver.getClass())) {
				channels.put(receiver.getClass(), new LinkedList<>());
			}
			channels.get(receiver.getClass()).add(channel);
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
