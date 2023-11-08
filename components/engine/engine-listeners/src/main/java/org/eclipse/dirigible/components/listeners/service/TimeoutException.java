package org.eclipse.dirigible.components.listeners.service;

public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimeoutException(String message) {
        super(message);
    }
}
