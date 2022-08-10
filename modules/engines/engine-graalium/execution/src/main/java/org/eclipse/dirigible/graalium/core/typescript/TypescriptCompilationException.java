package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.RuntimeException;

/**
 * The Class TypescriptCompilationException.
 */
class TypescriptCompilationException extends RuntimeException {
    
    /**
     * Instantiates a new typescript compilation exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public TypescriptCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}