/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Message;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;

@Component
public class Invoker {

    private DirigibleJavascriptCodeRunner runner;

    private final CamelProcessor processor;

    @Autowired
    public Invoker(CamelProcessor processor) {
        this.processor = processor;
    }

    public Object invoke(Message camelMessage) {
        resetCodeRunner();
        String resourcePath = (String) camelMessage.getExchange()
                                                   .getProperty("resource");
        String messageBody = camelMessage.getBody(String.class);

        var module = runner.run(Path.of(resourcePath));
        var result = runner.runMethod(module, "onMessage", messageBody);

        if (result.isNull()) {
            return Void.TYPE;
        } else if (result.isException()) {
            throw result.throwException();
        } else {
            return result.as(Object.class);
        }
    }

    private void resetCodeRunner() {
        close();
        runner = new DirigibleJavascriptCodeRunner();
    }

    @CalledFromJS
    public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
        return processor.invokeRoute(routeId, payload, headers);
    }

    public void close() {
        if (runner != null) {
            runner.close();
        }
    }
}
