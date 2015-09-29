package org.eclipse.dirigible.repository.ext.messaging;

public class EMessagingException extends Exception {

	private static final long serialVersionUID = 734247802124247902L;

	public EMessagingException() {
		//
	}

	public EMessagingException(String message) {
		super(message);
	}

	public EMessagingException(Throwable cause) {
		super(cause);
	}

	public EMessagingException(String message, Throwable cause) {
		super(message, cause);
	}

}
