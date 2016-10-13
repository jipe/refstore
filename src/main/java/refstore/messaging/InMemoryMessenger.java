package refstore.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryMessenger implements Messenger {

	private static final Logger log = LoggerFactory.getLogger(InMemoryMessenger.class);

	private final Map<String, Boolean> queues = new HashMap<>();
	private final Map<String, List<Receiver>> receivers = new HashMap<>();
	private final Map<String, Integer> indexes = new HashMap<>();

	private final ExecutorService executorService;

	private final AtomicBoolean closed = new AtomicBoolean();

	public InMemoryMessenger() {
		this(Executors.newSingleThreadExecutor());
	}

	public InMemoryMessenger(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void close() throws Exception {
		executorService.shutdownNow();
		closed.set(true);
	}

	@Override
	public synchronized void send(String queue, String message) {
		if (closed.get()) return;

		if (queues.containsKey(queue) && receivers.containsKey(queue)) {
			Receiver receiver = receivers.get(queue).get(indexes.get(queue));
			if (receiver.receive(message)) {
				indexes.put(queue, (indexes.get(queue) + 1) % receivers.get(queue).size());
			} else {
				receivers.get(queue).remove(receiver);
			}
		}
	}

	@Override
	public synchronized void broadcast(String message) {
		if (closed.get()) return;

		Map<String, List<Receiver>> finishedReceivers = new HashMap<>();
		for (Entry<String, List<Receiver>> entry : receivers.entrySet()) {
			String queue = entry.getKey();
			for (Receiver receiver : entry.getValue()) {
				if (!receiver.receive(message)) {
					if (!finishedReceivers.containsKey(queue)) {
						finishedReceivers.put(queue, new LinkedList<Receiver>());
					}
					finishedReceivers.get(queue).add(receiver);
				}
			}
		}
		for (Entry<String, List<Receiver>> entry : finishedReceivers.entrySet()) {
			String queue = entry.getKey();
			for (Receiver receiver : entry.getValue()) {
				receivers.get(queue).remove(receiver);
				indexes.put(queue, indexes.get(queue) % receivers.size());
			}
			if (receivers.get(queue).isEmpty()) {
				receivers.remove(queue);
				indexes.remove(queue);
			}
		}
	}

	@Override
	public synchronized void add(Receiver receiver) {
		if (closed.get()) return;

		for (String queue : receiver.getQueues()) {
			if (!queues.containsKey(queue)) continue;

			if (!receivers.containsKey(queue)) {
				receivers.put(queue, new LinkedList<Receiver>());
			}
			QueueingReceiver asyncReceiver = new QueueingReceiver(receiver, 100, executorService);
			receivers.get(queue).add(asyncReceiver);
			executorService.submit(asyncReceiver);
			if (!indexes.containsKey(queue)) {
				indexes.put(queue, 0);
			}
		}
	}

	@Override
	public void createQueue(String queue) {
		createQueue(queue, false);
	}

	@Override
	public void createQueue(String queue, boolean exclusive) {
		queues.put(queue, exclusive);
	}
}
