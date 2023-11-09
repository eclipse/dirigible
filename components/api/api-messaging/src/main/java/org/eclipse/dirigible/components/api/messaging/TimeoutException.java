package org.eclipse.dirigible.components.api.messaging;

/**
 * TimeoutException is used to throw an error when timeout to execute current operation
 */
public class TimeoutException extends MessagingAPIException {

    private static final long serialVersionUID = 1L;

    /**
     * Create instance of TimeoutException
     *
     * @param message error message
     * @param cause exception cause
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
