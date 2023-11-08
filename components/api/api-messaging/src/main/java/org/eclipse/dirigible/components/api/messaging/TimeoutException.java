package org.eclipse.dirigible.components.api.messaging;

public class TimeoutException extends MessagingAPIException {

    private static final long serialVersionUID = 1L;

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
