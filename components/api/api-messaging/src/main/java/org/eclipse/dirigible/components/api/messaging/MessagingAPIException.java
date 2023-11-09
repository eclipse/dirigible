package org.eclipse.dirigible.components.api.messaging;

/**
 * MessagingAPIException is used to throw an error when something unexpected occur
 */
public class MessagingAPIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create instance of MessagingAPIException
     *
     * @param message error message
     * @param cause exception cause
     */
    public MessagingAPIException(String message, Throwable cause) {
        super(message, cause);
    }

}
