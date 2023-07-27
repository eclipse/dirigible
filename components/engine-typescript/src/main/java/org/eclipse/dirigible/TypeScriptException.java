package org.eclipse.dirigible;

public class TypeScriptException extends RuntimeException {
    public TypeScriptException(String message) {
        super(message);
    }

    public TypeScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
