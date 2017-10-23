package org.eclipse.dirigible.core.indexing.api;

public class IndexingException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public IndexingException() {
		super();
	}

	public IndexingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IndexingException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexingException(String message) {
		super(message);
	}

	public IndexingException(Throwable cause) {
		super(cause);
	}

}
