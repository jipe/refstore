package refstore.messaging;

import java.util.UUID;

public class Receiver {

	private String receiverId;
	
	public Receiver() {
		this(UUID.randomUUID().toString());
	}
	
	public Receiver(String receiverId) {
		this.receiverId = receiverId;
	}
	
	public boolean receive(String message, Receiver replyTo) {
		return true;
	}

	public String getReceiverId() {
		return receiverId;
	}
}
