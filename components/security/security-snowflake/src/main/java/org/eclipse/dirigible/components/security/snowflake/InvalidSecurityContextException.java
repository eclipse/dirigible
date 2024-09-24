package org.eclipse.dirigible.components.security.snowflake;

public class InvalidSecurityContextException extends RuntimeException {

    public InvalidSecurityContextException(String message) {
        super(message);
    }
}
