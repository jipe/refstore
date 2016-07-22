package refstore.messaging;

import java.io.Closeable;
import java.io.IOException;

public interface MessageQueue extends Closeable {

	void requestHarvest() throws IOException;
	
}
