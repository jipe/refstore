package refstore.jobs;

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

		String fieldName = null;
		JsonParser parser = jsonFactory.createJsonParser(reader);
		JsonToken token = null;
		while ((token = parser.nextToken()) != null) {
			switch (token) {
			case FIELD_NAME:
				fieldName = parser.getText();
				break;
			case VALUE_STRING:
				Object o = target.peek();
				try {
					o.getClass().getDeclaredField(fieldName).set(o, parser.getText());
				} catch (Exception e) {
					throw new JobException("Error setting string value", e);
				}
				break;
			case VALUE_TRUE:
				break;
			case VALUE_FALSE:
				break;
			case START_OBJECT:
				if (fieldName != null) {
					try {
						target.push(getClass().getDeclaredField(fieldName).getType().newInstance());
					} catch (Exception e) {
						throw new JobException("Error instantiating field value", e);
					}
				}
				break;
			case END_OBJECT:
				if (fieldName != null) {
					target.pop();
					fieldName = null;
				}
				break;
			case VALUE_NUMBER_INT:
				break;
			case VALUE_NUMBER_FLOAT:
				break;
			case VALUE_NULL:
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
