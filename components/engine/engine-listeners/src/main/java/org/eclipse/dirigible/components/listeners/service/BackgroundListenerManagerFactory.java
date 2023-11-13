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

import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class BackgroundListenerManagerFactory {

    private final ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    @Autowired
    BackgroundListenerManagerFactory(ActiveMQConnectionArtifactsFactory connectionArtifactsFactory) {
        this.connectionArtifactsFactory = connectionArtifactsFactory;
    }

    BackgroundListenerManager create(Listener listener) {
        return new BackgroundListenerManager(listener, connectionArtifactsFactory);
    }
}
