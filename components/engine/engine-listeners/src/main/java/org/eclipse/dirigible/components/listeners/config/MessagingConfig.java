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
package org.eclipse.dirigible.components.listeners.config;

import javax.sql.DataSource;
import org.eclipse.dirigible.components.listeners.service.ListenersManager;
import org.eclipse.dirigible.components.listeners.service.MessageListenerManagerFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MessagingConfig {

    @Qualifier("SystemDB")
    @Autowired
    private DataSource dataSource;

    /** The repository. */
    @Autowired
    private IRepository repository;

    @Autowired
    private MessageListenerManagerFactory messageListenerManagerFactory;

    @Bean
    @Scope("singleton")
    public ListenersManager createSchedulerManager() throws Exception {
        ListenersManager schedulerManager = new ListenersManager(dataSource, repository, messageListenerManagerFactory);
        schedulerManager.initialize();
        return schedulerManager;
    }
}
