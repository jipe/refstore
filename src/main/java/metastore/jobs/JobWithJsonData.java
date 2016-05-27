package metastore.jobs;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

/**
 * Job class that will read/write data to and from all 
 * non-static, non-final, non-transient fields occurring
 * in any class that is a subclass of this class.
 * 
 * The data is stored in a JSON structure and read/written from/to
 * the given reader/writer.
 */
public abstract class JobWithJsonData extends Job {

	private final JsonFactory jsonFactory;

	public JobWithJsonData(JsonFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
	}

	@Override
	public void readData(Reader reader) throws JobException, IOException {
		Stack<Object> target = new Stack<>();
		target.push(this);

		JsonParser parser = jsonFactory.createJsonParser(reader);
		JsonToken token = null;
		while ((token = parser.nextToken()) != null) {
			switch (token) {
			case START_OBJECT:
				break;
			case END_OBJECT:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void writeData(Writer writer) throws JobException, IOException {
		// TODO Auto-generated method stub

	}

}
