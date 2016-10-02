package refstore.harvesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.messaging.Receiver;

public class HarvestingMessageListener implements Receiver {
	
	private static final Logger log = LoggerFactory.getLogger(HarvestingMessageListener.class);
	
	@Override
	public boolean receive(String message) {
//		log.info("Received message '{}'", message);
		return true;
	}

}
