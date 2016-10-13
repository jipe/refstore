package refstore.messaging;

public interface Messenger extends AutoCloseable {

	public void send(String queue, String message);

	public void broadcast(String message);

	public void add(Receiver receiver);

	public void createQueue(String queue);
	public void createQueue(String queue, boolean exclusive);

}
