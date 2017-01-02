package refstore.harvesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import refstore.messaging.Receiver;

public class HarvestingReceiver extends Receiver {
	
	public static final String QUEUE = "harvest_requests";
	
	private static final Logger log = LoggerFactory.getLogger(HarvestingReceiver.class);
	
	public HarvestingReceiver() {
		super(HarvestingReceiver.QUEUE);
	}
	
	@Override
	public boolean receive(String message) {
		log.info("Received message: {}", message);
		return true;
	}

}
