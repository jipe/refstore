package refstore.messaging;

import java.io.Closeable;

public interface Messenger extends Closeable, AutoCloseable {

	/**
	 * Send a message to all receivers of type {@code receiverType}
	 * 
	 * @param receiverType
	 * @param message
	 */
	void broadcast(Class<? extends Receiver> receiverType, String message);
	
	/**
	 * Send a message on a round-robin basis to one of the receivers registered with type {@code receiverType}
	 * @param receiverType
	 * @param message
	 */
	void send(Class<? extends Receiver> receiverType, String message);
	
	/**
	 * Add a message receiver
	 * 
	 * @param receiver
	 */
	void add(Receiver receiver);
	
}
