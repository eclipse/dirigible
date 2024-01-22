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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.Synchronization;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.graalvm.polyglot.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Scope("prototype")
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

        if (result != null) {
            camelMessage.getExchange()
                        .setMessage(unwrapCamelMessage(result));
            camelMessage.getExchange()
                        .getExchangeExtension()
                        .addOnCompletion(new Synchronization() {
                            @Override
                            public void onComplete(Exchange exchange) {
                                closeRunner();
                            }

                            @Override
                            public void onFailure(Exchange exchange) {
                                closeRunner();
                            }
                        });
        } else {
            closeRunner();
        }
    }

    private IntegrationMessage wrapCamelMessage(Message camelMessage) {
        return new IntegrationMessage(camelMessage);
    }

    private Message unwrapCamelMessage(Value value) {
        validateIntegrationMessage(value);
        IntegrationMessage message = value.asHostObject();
        return message.getCamelMessage();
    }

    private void validateIntegrationMessage(Value value) {
        if (!value.isHostObject() || !(value.asHostObject() instanceof IntegrationMessage)) {
            throw new IllegalArgumentException(
                    "Unexpected return received from @dirigible/integrations::onMessage(). Expected return type: IntegrationMessage.");
        }
    }

    @CalledFromJS
    public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
        return processor.invokeRoute(routeId, payload, headers);
    }

    private void resetCodeRunner() {
        runner = new DirigibleJavascriptCodeRunner();
    }

    private void closeRunner() {
        if (runner != null) {
            runner.close();
        }
    }
}
