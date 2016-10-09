package refstore.messaging;

import java.io.Closeable;

public interface Messenger extends Closeable, AutoCloseable {

	String getNodeId();
	
	void ping(Receiver replyTo);
	
	void ping(Class<? extends Receiver> receiverType, Receiver replyTo);
	
	/**
	 * Send a message to all registered receivers.
	 *
	 * @param message
	 * @param replyTo
	 */
	void broadcast(String message, Receiver replyTo);

	/**
	 * Send a message to all receivers of type {@code receiverType}
	 * 
	 * @param receiverType
	 * @param message
	 * @param replyTo
	 */
	void broadcast(Class<? extends Receiver> receiverType, String message, Receiver replyTo);

	/**
	 * Send a message on a round-robin basis to one of the receivers registered with type {@code receiverType}
	 * @param receiverType
	 * @param message
	 * @param replyTo
	 */
	void send(Class<? extends Receiver> receiverType, String message, Receiver replyTo);

	/**
	 * Add a message receiver
	 * 
	 * @param receiver
	 */
	void add(Receiver receiver);

}
