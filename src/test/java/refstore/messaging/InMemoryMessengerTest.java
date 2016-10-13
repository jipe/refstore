package refstore.messaging;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InMemoryMessengerTest {

	Messenger messenger;

	@Before
	public void setUpMessenger() throws Exception {
		messenger = new InMemoryMessenger();

		messenger.createQueue("queue");
		messenger.createQueue("other-queue");
	}

	@After
	public void tearDownMessenger() throws Exception {
		messenger.close();
	}

	@Test
	public void sendsMessages() throws Exception {
		Receiver receiver = createReceiver("queue");

		messenger.add(receiver);
		messenger.send("queue", "message");

		Thread.sleep(100);

		verify(receiver).receive("message");
	}

	@Test
	public void sendsOnlyToSubscribedReceivers() throws Exception {
		Receiver receiver1 = createReceiver("queue");
		Receiver receiver2 = createReceiver("other-queue");

		messenger.add(receiver1);
		messenger.add(receiver2);
		messenger.send("queue", "message");

		Thread.sleep(100);

		verify(receiver1).receive("message");
		verify(receiver2, never()).receive("message");
	}

	@Test
	public void receiversStayActiveAfterReceive() throws Exception {
		Receiver receiver = createReceiver("queue");

		messenger.add(receiver);
		messenger.send("queue", "message1");
		messenger.send("queue", "message2");

		Thread.sleep(100);

		verify(receiver).receive("message1");
		verify(receiver).receive("message2");
	}

	@Test
	public void sendsUsingRoundRobinDispatch() throws Exception {
		Receiver receiver1 = createReceiver("queue");
		Receiver receiver2 = createReceiver("queue");
		Receiver receiver3 = createReceiver("queue");

		InOrder inOrder = inOrder(receiver1, receiver2, receiver3);

		messenger.add(receiver1);
		messenger.add(receiver2);
		messenger.add(receiver3);

		messenger.send("queue", "message");
		messenger.send("queue", "message");
		messenger.send("queue", "message");

		Thread.sleep(100);

		inOrder.verify(receiver1).receive("message");
		inOrder.verify(receiver2).receive("message");
		inOrder.verify(receiver3).receive("message");
	}

	@Test
	public void sendsOnlyToCreatedQueues() throws Exception {
		Receiver receiver = createReceiver("unknown-queue");

		messenger.add(receiver);
		messenger.send("unknown-queue", "message");

		Thread.sleep(100);

		verify(receiver, never()).receive("message");
	}

	@Test
	public void broadcastsMessages() throws Exception {
		Receiver receiver1 = createReceiver("queue");
		Receiver receiver2 = createReceiver("other-queue");

		messenger.add(receiver1);
		messenger.add(receiver2);
		messenger.broadcast("message");

		Thread.sleep(100);

		verify(receiver1).receive("message");
		verify(receiver2).receive("message");
	}

	@Test
	public void broadcastsOnlyToCreatedQueues() throws Exception {
		Receiver receiver1 = createReceiver("queue");
		Receiver receiver2 = createReceiver("unknown-queue");

		messenger.add(receiver1);
		messenger.add(receiver2);

		messenger.broadcast("message");

		Thread.sleep(100);

		verify(receiver1).receive("message");
		verify(receiver2, never()).receive("message");
	}

	private Receiver createReceiver(String... queues) {
		Receiver receiver = mock(Receiver.class);
		when(receiver.getQueues()).thenReturn(Arrays.asList(queues));
		when(receiver.receive(anyString())).thenReturn(true);
		return receiver;
	}
}
