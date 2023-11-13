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
package org.eclipse.dirigible.components.listeners.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

@Component
class ListenersShutdownApplicationListener implements ApplicationListener<ApplicationEvent> {

    private final BackgroundListenersManager listenersManager;

    @Autowired
    ListenersShutdownApplicationListener(BackgroundListenersManager listenersManager) {
        this.listenersManager = listenersManager;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (isApplicableEvent(event)) {
            listenersManager.stopListeners();
        }
    }

    private boolean isApplicableEvent(ApplicationEvent event) {
        return event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent;
    }
}
