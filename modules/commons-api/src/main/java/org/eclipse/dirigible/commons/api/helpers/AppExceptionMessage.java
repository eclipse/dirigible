package org.eclipse.dirigible.commons.api.helpers;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Error message that is returned if an application error occur.
 */
@XmlRootElement
public class AppExceptionMessage {

	private String message;

	private int status;

	/**
	 * Needed for serialization / de-serialization.
	 */
	public AppExceptionMessage() {
	}

	/**
	 * Default constructor.
	 *
	 * @param status
	 *            the response server status.
	 * @param message
	 *            the actual error message.
	 */
	public AppExceptionMessage(Status status, String message) {
		this.status = status.getStatusCode();
		this.message = message;
	}

	/**
	 * @return the error message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message.
	 *
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the server response status.
	 *
	 * @return the server response status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the server response status.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
