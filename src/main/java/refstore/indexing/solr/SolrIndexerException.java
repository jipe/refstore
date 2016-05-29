package refstore.indexing.solr;

import refstore.indexing.IndexerException;

public class SolrIndexerException extends IndexerException {

	private static final long serialVersionUID = 1L;

	public SolrIndexerException() {
		super();
	}

	public SolrIndexerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SolrIndexerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolrIndexerException(String message) {
		super(message);
	}

	public SolrIndexerException(Throwable cause) {
		super(cause);
	}

}
