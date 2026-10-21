/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * A factory for the callbacks a listener subscribes with: the message listener and the connection's
 * exception listener, both handing over to the listener's script handler.
 */
@Component
class AsynchronousMessageListenerFactory {

    /** The tenant property manager. */
    private final TenantPropertyManager tenantPropertyManager;

    /** The tenant context. */
    private final TenantContext tenantContext;

    /** The executor of the listeners' script handlers, absent where no script engine is deployed. */
    private final ObjectProvider<ListenerScriptExecutor> scriptExecutor;

    /**
     * Instantiates a new asynchronous message listener factory.
     *
     * @param tenantPropertyManager the tenant property manager
     * @param tenantContext the tenant context
     * @param scriptExecutor the executor of the listeners' script handlers
     */
    AsynchronousMessageListenerFactory(TenantPropertyManager tenantPropertyManager, TenantContext tenantContext,
            ObjectProvider<ListenerScriptExecutor> scriptExecutor) {
        this.tenantPropertyManager = tenantPropertyManager;
        this.tenantContext = tenantContext;
        this.scriptExecutor = scriptExecutor;
    }

    /**
     * Creates the message listener.
     *
     * @param listenerDescriptor the listener descriptor
     * @return the asynchronous message listener
     * @throws ScriptEngineUnavailableException when no script engine is deployed
     */
    AsynchronousMessageListener create(ListenerDescriptor listenerDescriptor) {
        return new AsynchronousMessageListener(listenerDescriptor, tenantPropertyManager, tenantContext,
                scriptExecutor(listenerDescriptor));
    }

    /**
     * Creates the exception listener of the listener's connection.
     *
     * @param listenerDescriptor the listener descriptor
     * @return the exception listener
     * @throws ScriptEngineUnavailableException when no script engine is deployed
     */
    ListenerExceptionHandler createExceptionHandler(ListenerDescriptor listenerDescriptor) {
        return new ListenerExceptionHandler(listenerDescriptor.getHandlerPath(), scriptExecutor(listenerDescriptor));
    }

    private ListenerScriptExecutor scriptExecutor(ListenerDescriptor listenerDescriptor) {
        ListenerScriptExecutor executor = scriptExecutor.getIfAvailable();
        if (executor == null) {
            throw new ScriptEngineUnavailableException(listenerDescriptor.getHandlerPath());
        }
        return executor;
    }
}
