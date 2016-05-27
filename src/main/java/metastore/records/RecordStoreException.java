package metastore.records;

import java.io.IOException;

public class RecordStoreException extends IOException {

	private static final long serialVersionUID = 1L;

	public RecordStoreException() {
		super();
	}

	public RecordStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecordStoreException(String message) {
		super(message);
	}

	public RecordStoreException(Throwable cause) {
		super(cause);
	}

}
