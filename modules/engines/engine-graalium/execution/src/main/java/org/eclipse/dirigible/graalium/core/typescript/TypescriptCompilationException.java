package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.RuntimeException;

class TypescriptCompilationException extends RuntimeException {
    public TypescriptCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}