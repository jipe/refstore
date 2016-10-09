package refstore.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class SimpleMessenger implements Messenger {
	
	private final String nodeId = UUID.randomUUID().toString();
	
	private final Map<Class<? extends Receiver>, List<Receiver>> receivers = new HashMap<>();
	private final Map<Class<? extends Receiver>, Integer> receiverIndexes = new HashMap<>();

	@Override
	public void close() {
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}
	
	@Override
	public void ping(Receiver replyTo) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void ping(Class<? extends Receiver> receiverType, Receiver replyTo) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void broadcast(String message, Receiver replyTo) {
		for (Entry<Class<? extends Receiver>, List<Receiver>> entry : receivers.entrySet()) {
			for (Receiver receiver : entry.getValue()) {
				receiver.receive(message, replyTo);
			}
		}
	}
	
	@Override
	public synchronized void broadcast(Class<? extends Receiver> receiverType, String message, Receiver replyTo) {
		if (receivers.containsKey(receiverType)) {
			for (Receiver listener : receivers.get(receiverType)) {
				listener.receive(message, replyTo);
			}
		}
	}

	@Override
	public synchronized void send(Class<? extends Receiver> receiverType, String message, Receiver replyTo) {
		if (receivers.containsKey(receiverType)) {
			int index = receiverIndexes.get(receiverType);
			receivers.get(receiverType).get(index).receive(message, replyTo);
			receiverIndexes.put(receiverType, (index + 1) % receivers.size());
		}
	}

	@Override
	public synchronized void add(Receiver receiver) {
		if (!receivers.containsKey(receiver.getClass())) {
			receivers.put(receiver.getClass(), new LinkedList<>());
			receiverIndexes.put(receiver.getClass(), 0);
		}
		receivers.get(receiver.getClass()).add(receiver);
	}

}
