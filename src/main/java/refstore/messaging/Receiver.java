package refstore.messaging;

public interface Receiver {

	boolean receive(String message);
	
}
