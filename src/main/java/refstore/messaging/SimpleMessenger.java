package refstore.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleMessenger implements Messenger {
	
	private final Map<Class<? extends Receiver>, List<Receiver>> messageListeners = new HashMap<>();
	private final Map<Class<? extends Receiver>, Integer> messageListenerIndexes = new HashMap<>();

	@Override
	public void close() {
	}

	@Override
	public synchronized void broadcast(Class<? extends Receiver> receiverType, String message) {
		if (messageListeners.containsKey(receiverType)) {
			for (Receiver listener : messageListeners.get(receiverType)) {
				listener.receive(message);
			}
		}
	}

	@Override
	public synchronized void send(Class<? extends Receiver> receiverType, String message) {
		if (messageListeners.containsKey(receiverType)) {
			int index = messageListenerIndexes.get(receiverType);
			messageListeners.get(receiverType).get(index).receive(message);
			messageListenerIndexes.put(receiverType, (index + 1) % messageListeners.size());
		}
	}

	@Override
	public synchronized void add(Receiver listener) {
		if (!messageListeners.containsKey(listener.getClass())) {
			messageListeners.put(listener.getClass(), new LinkedList<>());
			messageListenerIndexes.put(listener.getClass(), 0);
		}
		messageListeners.get(listener.getClass()).add(listener);
	}

}
