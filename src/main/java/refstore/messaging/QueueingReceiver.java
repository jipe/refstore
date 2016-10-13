package refstore.messaging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueingReceiver extends Receiver implements Runnable {

	private final BlockingQueue<String> queue;
	private final Receiver receiver;
	private final ExecutorService executorService;

	public QueueingReceiver(Receiver receiver, int capacity, ExecutorService executorService) {
		this.executorService = executorService;
		this.queue = new ArrayBlockingQueue<>(capacity);
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			String message = queue.poll(10, TimeUnit.MILLISECONDS);
			if (message != null) {
				receiver.receive(message);
			}
			executorService.submit(this);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public boolean receive(String message) {
		try {
			queue.put(message);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

}
