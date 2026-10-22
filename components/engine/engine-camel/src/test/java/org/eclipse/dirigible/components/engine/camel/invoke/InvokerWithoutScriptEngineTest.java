/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.invoke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the {@link Invoker} routes name starts - it
 * used to need the JavaScript invoker to be constructed at all - and a route step handing a message
 * to a script fails with the missing engine as its reason.
 */
class InvokerWithoutScriptEngineTest {

    private static final String HANDLER = "project/route-handler.js";

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withBean(CamelProcessor.class, () -> mock(CamelProcessor.class))
                                          .withUserConfiguration(Invoker.class);

    @Test
    void aScriptStepFailsNamingTheMissingEngine() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            Exchange exchange = mock(Exchange.class);
            when(exchange.getProperty("resource")).thenReturn(HANDLER);
            Message message = mock(Message.class);
            when(message.getExchange()).thenReturn(exchange);

            assertThatThrownBy(() -> context.getBean(Invoker.class)
                                            .invoke(message)).isInstanceOf(ScriptEngineUnavailableException.class)
                                                             .hasMessageContaining(HANDLER)
                                                             .hasMessageContaining("JavaScript engine");
        });
    }
}
