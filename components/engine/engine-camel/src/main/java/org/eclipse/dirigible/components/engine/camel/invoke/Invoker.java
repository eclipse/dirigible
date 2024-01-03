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
import org.graalvm.polyglot.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    public void invoke(Message camelMessage) throws IOException {
        resetCodeRunner();
        String resourcePath = (String) camelMessage.getExchange()
                                                   .getProperty("resource");

        var module = runner.run(Path.of(resourcePath));
        var result = runner.runMethod(module, "onMessage", wrapCamelMessage(camelMessage));

        camelMessage.getExchange()
                    .setMessage(unwrapCamelMessage(result));
    }

    private IntegrationMessage wrapCamelMessage(Message camelMessage) {
        return new IntegrationMessage(camelMessage);
    }

    private Message unwrapCamelMessage(Value value) {
        if (value == null || !value.isHostObject()) {
            throw new IllegalStateException("Unexpected null value or return type received from " +
                    "@dirigible/integrations::onMessage(). " +
                    "Expected return type: org.eclipse.dirigible.components.engine.camel.invoke.IntegrationMessage");
        }

        Object hostObject = value.asHostObject();

        if (!(hostObject instanceof IntegrationMessage integrationMessage)) {
            throw new IllegalArgumentException("Unexpected return type received from " +
                    "@dirigible/integrations::onMessage(). " +
                    "Expected return type: org.eclipse.dirigible.components.engine.camel.invoke.IntegrationMessage");
        }

        if (integrationMessage.getCamelMessage() == null) {
            throw new IllegalStateException("IntegrationMessage does not contain a valid Camel Message. " +
                    "Expected a non-null Camel Message.");
        }

        return integrationMessage.getCamelMessage();
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
