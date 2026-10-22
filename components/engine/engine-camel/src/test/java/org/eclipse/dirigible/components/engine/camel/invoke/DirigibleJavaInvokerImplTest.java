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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.dirigible.components.engine.camel.components.DirigibleJavaException;
import org.eclipse.dirigible.engine.java.runtime.ClientBeanFactory;
import org.eclipse.dirigible.engine.java.runtime.ClientBeansHolder;
import org.eclipse.dirigible.engine.java.runtime.ClientClassLoader;
import org.eclipse.dirigible.engine.java.runtime.ClientClassLoaderHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A client Java {@link Processor} of a {@code dirigible-java} route step is built the way a BPMN
 * {@code JavaDelegate} is (#8017): wired by the client bean container when it declares an injection
 * point, by its no-arg constructor otherwise.
 */
class DirigibleJavaInvokerImplTest {

    private final ClientClassLoaderHolder classLoaderHolder = new ClientClassLoaderHolder();

    private final ClientBeansHolder beansHolder = new ClientBeansHolder();

    private final ClientBeanFactory container = mock(ClientBeanFactory.class);

    private final DirigibleJavaInvokerImpl invoker = new DirigibleJavaInvokerImpl(classLoaderHolder, beansHolder);

    private final Exchange exchange = mock(Exchange.class);

    @BeforeEach
    void installAGeneration() {
        classLoaderHolder.swap(new ClientClassLoader(getClass().getClassLoader(), Map.of()));
        beansHolder.swap(container);
    }

    @Test
    void aProcessorDeclaringACollaboratorIsBuiltByTheContainer() {
        when(container.createUnmanaged(GreetingProcessor.class)).thenReturn(Optional.of(new GreetingProcessor(() -> "injected")));

        invoker.invoke(exchange, GreetingProcessor.class.getName());

        verify(exchange).setProperty("greeting", "injected");
    }

    @Test
    void aProcessorDeclaringNothingToInjectIsBuiltByItsNoArgConstructor() {
        when(container.createUnmanaged(PlainProcessor.class)).thenReturn(Optional.empty());

        invoker.invoke(exchange, PlainProcessor.class.getName());

        verify(exchange).setProperty("greeting", "plain");
    }

    @Test
    void beforeTheFirstGenerationAProcessorIsBuiltByItsNoArgConstructor() {
        beansHolder.swap(null);

        invoker.invoke(exchange, PlainProcessor.class.getName());

        verify(exchange).setProperty("greeting", "plain");
    }

    @Test
    void anUnsatisfiableCollaboratorFailsTheExchangeNamingTheProcessor() {
        when(container.createUnmanaged(GreetingProcessor.class)).thenThrow(new IllegalStateException("No client bean of type Greeter"));

        assertThatThrownBy(() -> invoker.invoke(exchange, GreetingProcessor.class.getName())).isInstanceOf(DirigibleJavaException.class)
                                                                                             .hasMessageContaining(
                                                                                                     GreetingProcessor.class.getName())
                                                                                             .hasRootCauseMessage(
                                                                                                     "No client bean of type Greeter");
    }

    interface Greeter {
        String greet();
    }

    static class GreetingProcessor implements Processor {

        private final Greeter greeter;

        GreetingProcessor(Greeter greeter) {
            this.greeter = greeter;
        }

        @Override
        public void process(Exchange exchange) {
            exchange.setProperty("greeting", greeter.greet());
        }
    }

    public static class PlainProcessor implements Processor {

        @Override
        public void process(Exchange exchange) {
            exchange.setProperty("greeting", "plain");
        }
    }
}
