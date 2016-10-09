package refstore.harvesting;

import refstore.messaging.Receiver;

public class HarvestingReceiver extends Receiver {
	
	public HarvestingReceiver(String nodeId) {
		super(nodeId);
	}
	
	@Override
	public boolean receive(String message, Receiver replyTo) {
		return true;
	}

}
