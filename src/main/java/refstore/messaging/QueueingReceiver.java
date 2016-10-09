package refstore.messaging;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueingReceiver extends Receiver {

	private static final Logger log = LoggerFactory.getLogger(QueueingReceiver.class);
	
	private final BlockingQueue<String> messages;
	
	public QueueingReceiver(BlockingQueue<String> messages) {
		this.messages   = messages;
	}
	
	@Override
	public boolean receive(String message, Receiver replyTo) {
		try {
			messages.put(message);
		} catch (InterruptedException e) {
			log.info("Interrupted while waiting to receive message");
		}
		return true;
	}
	
}
