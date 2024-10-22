/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.Synchronization;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.graalvm.polyglot.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;

/**
 * The Class Invoker.
 */
@Component
public class Invoker {

    /** The processor. */
    private final CamelProcessor processor;

    /**
     * Instantiates a new invoker.
     *
     * @param processor the processor
     */
    @Autowired
    public Invoker(CamelProcessor processor) {
        this.processor = processor;
    }

    /**
     * Invoke.
     *
     * @param camelMessage the camel message
     */
    public void invoke(Message camelMessage) {
        String resourcePath = (String) camelMessage.getExchange()
                                                   .getProperty("resource");
        invoke(camelMessage, resourcePath);
    }

    public void invoke(Message camelMessage, String resourcePath) {
        DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner();

        var module = runner.run(Path.of(resourcePath));
        var result = runner.runMethod(module, "onMessage", wrapCamelMessage(camelMessage));

        if (result != null) {
            camelMessage.getExchange()
                        .setMessage(unwrapCamelMessage(result));
            camelMessage.getExchange()
                        .getExchangeExtension()
                        .addOnCompletion(new Synchronization() {
                            @Override
                            public void onComplete(Exchange exchange) {
                                runner.close();
                            }

                            @Override
                            public void onFailure(Exchange exchange) {
                                runner.close();
                            }
                        });
        } else {
            runner.close();
        }
    }

    /**
     * Wrap camel message.
     *
     * @param camelMessage the camel message
     * @return the integration message
     */
    private IntegrationMessage wrapCamelMessage(Message camelMessage) {
        return new IntegrationMessage(camelMessage);
    }

    /**
     * Unwrap camel message.
     *
     * @param value the value
     * @return the message
     */
    private Message unwrapCamelMessage(Value value) {
        validateIntegrationMessage(value);
        IntegrationMessage message = value.asHostObject();
        return message.getCamelMessage();
    }

    /**
     * Validate integration message.
     *
     * @param value the value
     */
    private void validateIntegrationMessage(Value value) {
        if (!value.isHostObject() || !(value.asHostObject() instanceof IntegrationMessage)) {
            throw new IllegalArgumentException(
                    "Unexpected return received from sdk/integrations::onMessage(). Expected return type: IntegrationMessage.");
        }
    }

    /**
     * Invoke route.
     *
     * @param routeId the route id
     * @param payload the payload
     * @param headers the headers
     * @return the object
     */
    @CalledFromJS
    public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
        return processor.invokeRoute(routeId, payload, headers);
    }
}
