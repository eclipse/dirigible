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

import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.dirigible.components.engine.camel.components.DirigibleJavaException;
import org.eclipse.dirigible.components.engine.camel.components.DirigibleJavaInvoker;
import org.eclipse.dirigible.engine.java.runtime.ClientBeanFactory;
import org.eclipse.dirigible.engine.java.runtime.ClientBeansHolder;
import org.eclipse.dirigible.engine.java.runtime.ClientClassLoader;
import org.eclipse.dirigible.engine.java.runtime.ClientClassLoaderHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves and executes a client Java {@link Processor} referenced by a {@code dirigible-java}
 * route step.
 *
 * <p>
 * The fully-qualified class name is resolved through the currently installed
 * {@link ClientClassLoader} (managed by {@link ClientClassLoaderHolder}) on every invocation, so a
 * recompiled handler is picked up without a server restart. A fresh processor is built for every
 * exchange and invoked with the Camel {@link Exchange}. Counterpart of the JavaScript invoker.
 *
 * <p>
 * The processor is built the way a BPMN {@code JavaDelegate} is: through the client bean container
 * ({@link ClientBeanFactory#createUnmanaged(Class)}), so its constructor parameters and
 * {@code @Inject} fields receive the live client beans - while it never becomes a bean itself. A
 * processor declaring no injection point is built by its public no-arg constructor, exactly as
 * before.
 */
@Component
class DirigibleJavaInvokerImpl implements DirigibleJavaInvoker {

    private final ClientClassLoaderHolder clientClassLoaderHolder;

    private final ClientBeansHolder clientBeansHolder;

    DirigibleJavaInvokerImpl(ClientClassLoaderHolder clientClassLoaderHolder, ClientBeansHolder clientBeansHolder) {
        this.clientClassLoaderHolder = clientClassLoaderHolder;
        this.clientBeansHolder = clientBeansHolder;
    }

    @Override
    public void invoke(Exchange exchange, String className) {
        if (className == null || className.isBlank()) {
            throw new DirigibleJavaException("Class name cannot be blank at the dirigible-java component.");
        }
        String fqn = className.trim();

        ClientClassLoader loader = clientClassLoaderHolder.current();
        if (loader == null) {
            throw new DirigibleJavaException("No client Java code has been compiled yet; cannot resolve handler [" + fqn
                    + "]. Add a .java source under the project's registry path and let the synchronizer pick it up.");
        }

        Class<?> handlerClass;
        try {
            handlerClass = loader.loadClass(fqn);
        } catch (ClassNotFoundException e) {
            throw new DirigibleJavaException("Client Java class [" + fqn + "] is not loaded. Ensure the source is present under "
                    + "/registry/public/<project>/ and compiles successfully.", e);
        }

        if (!Processor.class.isAssignableFrom(handlerClass)) {
            throw new DirigibleJavaException("Client Java class [" + fqn + "] does not implement " + Processor.class.getName() + ".");
        }

        Processor processor = instantiate(handlerClass, fqn);

        try {
            processor.process(exchange);
        } catch (Exception e) {
            throw new DirigibleJavaException("Exception during execution of client Java class [" + fqn + "].", e);
        }
    }

    /**
     * Build the processor for this exchange: wired by the client bean container when it declares an
     * injection point, otherwise by its public no-arg constructor. A dependency the container cannot
     * satisfy fails this exchange, not the route.
     */
    private Processor instantiate(Class<?> handlerClass, String fqn) {
        Optional<Processor> wired = wire(handlerClass, fqn);
        if (wired.isPresent()) {
            return wired.get();
        }
        try {
            return (Processor) handlerClass.getDeclaredConstructor()
                                           .newInstance();
        } catch (ReflectiveOperationException e) {
            throw new DirigibleJavaException(
                    "Failed to instantiate client Java class [" + fqn + "]. A public no-arg constructor is required.", e);
        }
    }

    private Optional<Processor> wire(Class<?> handlerClass, String fqn) {
        ClientBeanFactory container = clientBeansHolder.current();
        if (container == null) {
            return Optional.empty();
        }
        try {
            return container.createUnmanaged(handlerClass)
                            .map(Processor.class::cast);
        } catch (RuntimeException e) {
            throw new DirigibleJavaException("Failed to inject the dependencies of client Java class [" + fqn + "].", e);
        }
    }

}
