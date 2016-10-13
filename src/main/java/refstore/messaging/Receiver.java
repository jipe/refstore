package refstore.messaging;

import java.util.LinkedList;
import java.util.List;

public abstract class Receiver {

	private final List<String> queues = new LinkedList<>();

	public Receiver(String... queues) {
		for (String queue : queues) {
			this.queues.add(queue);
		}
	}

	public Receiver withQueue(String queue) {
		this.queues.add(queue);
		return this;
	}

	public List<String> getQueues() {
		return queues;
	}

	public abstract boolean receive(String message);

}
