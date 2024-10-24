package org.eclipse.dirigible.components.engine.camel.components;

import org.apache.camel.RuntimeCamelException;

public class DirigibleJavaScriptException extends RuntimeCamelException {

    public DirigibleJavaScriptException(String message) {
        super(message);
    }

    public DirigibleJavaScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
