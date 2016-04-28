package metastore.jobs;

public class JobStoreException extends Exception {

    private static final long serialVersionUID = 1L;

    public JobStoreException() {
        super();
    }

    public JobStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JobStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobStoreException(String message) {
        super(message);
    }

    public JobStoreException(Throwable cause) {
        super(cause);
    }
    
    
}
