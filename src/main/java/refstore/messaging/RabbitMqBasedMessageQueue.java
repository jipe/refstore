package refstore.messaging;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqBasedMessageQueue implements MessageQueue {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqBasedMessageQueue.class);
	
	private Connection connection;
	
	public RabbitMqBasedMessageQueue(String rabbitMqUrl) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(rabbitMqUrl);
		connection = factory.newConnection();
	}
	
	@Override
	public void requestHarvest() throws IOException {
		try {
			Channel channel = connection.createChannel();
			channel.exchangeDeclare("requests", "direct", true);
			channel.basicPublish("requests", "harvest", null, "Hello".getBytes("UTF-8"));
			channel.close();
		} catch (TimeoutException e) {
			log.warn("Error closing channel", e);
		}
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}
	
}
