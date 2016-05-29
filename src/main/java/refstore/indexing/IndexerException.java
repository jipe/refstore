package refstore.indexing;

public class IndexerException extends Exception {

	private static final long serialVersionUID = 1L;

	public IndexerException() {
		super();
	}

	public IndexerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IndexerException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexerException(String message) {
		super(message);
	}

	public IndexerException(Throwable cause) {
		super(cause);
	}

}
