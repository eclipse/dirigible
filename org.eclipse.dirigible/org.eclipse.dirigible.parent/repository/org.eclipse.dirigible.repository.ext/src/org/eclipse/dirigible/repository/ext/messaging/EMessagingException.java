package org.eclipse.dirigible.repository.ext.messaging;

/**
 * Specialized exception for the messaging hub
 */
public class EMessagingException extends Exception {

	private static final long serialVersionUID = 734247802124247902L;

	/**
	 * The default constructor
	 */
	public EMessagingException() {
		//
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 */
	public EMessagingException(String message) {
		super(message);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param cause
	 */
	public EMessagingException(Throwable cause) {
		super(cause);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 * @param cause
	 */
	public EMessagingException(String message, Throwable cause) {
		super(message, cause);
	}

}
