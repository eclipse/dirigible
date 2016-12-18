package org.eclipse.dirigible.repository.ext.db.model;

/**
 * Specialized exception for the data models
 */
public class EDataStructureModelFormatException extends Exception {

	private static final long serialVersionUID = 8008932847050301958L;

	/**
	 * The default constructor
	 */
	public EDataStructureModelFormatException() {
		super();
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EDataStructureModelFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 * @param cause
	 */
	public EDataStructureModelFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 */
	public EDataStructureModelFormatException(String message) {
		super(message);
	}

	/**
	 * Overloaded constructor
	 * 
	 * @param cause
	 */
	public EDataStructureModelFormatException(Throwable cause) {
		super(cause);
	}

}
