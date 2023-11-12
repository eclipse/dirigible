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

import javax.jms.Session;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerManagerFactory {

    private final Session session;

    @Autowired
    MessageListenerManagerFactory(@Qualifier("ActiveMQSession") Session session) {
        this.session = session;
    }

    MessageListenerManager create(Listener listener) {
        return new MessageListenerManager(listener, session);
    }
}
