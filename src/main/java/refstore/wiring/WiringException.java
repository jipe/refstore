package refstore.wiring;

public class WiringException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WiringException() {
		super();
	}

	public WiringException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WiringException(String message, Throwable cause) {
		super(message, cause);
	}

	public WiringException(String message) {
		super(message);
	}

	public WiringException(Throwable cause) {
		super(cause);
	}

}
