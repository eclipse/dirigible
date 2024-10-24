package org.eclipse.dirigible.components.engine.camel.components;

import org.apache.camel.Message;

public interface DirigibleJavaScriptInvoker {

    /**
     * Invoke dirigible JavaScript file
     *
     * @param camelMessage camel message
     * @param javaScriptPath a path to the JavaScript which should be executed
     */
    void invoke(Message camelMessage, String javaScriptPath);
}
