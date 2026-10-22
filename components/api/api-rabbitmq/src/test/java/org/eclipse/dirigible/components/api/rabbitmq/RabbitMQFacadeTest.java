/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.rabbitmq;

import nl.altindag.log.LogCaptor;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.script.ScriptExecutor;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@Tag("testcontainers")
@DisabledOnOs(OS.WINDOWS)
@Testcontainers
public class RabbitMQFacadeTest {

    private static final String message = "testMessage";
    private static final String queue = "test-queue";

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.8.19-alpine");

    LogCaptor logCaptor = LogCaptor.forClass(RabbitMQFacade.class);

    @BeforeAll
    static void setUp() {
        Configuration.set("DIRIGIBLE_RABBITMQ_CLIENT_URI", rabbit.getHost() + ":" + rabbit.getFirstMappedPort());
    }

    @Test
    public void send() {
        logCaptor.setLogLevelToInfo();

        RabbitMQFacade.send(queue, message);
        assertEquals(logCaptor.getInfoLogs()
                              .get(0),
                "Sent: " + "'" + message + "'" + " to [" + queue + "]");

    }

    @Test
    public void rabbitMQIntegration() {
        logCaptor.setLogLevelToInfo();

        // The handler is a script, so listening needs a script engine to hand the messages to.
        new ApplicationContextRunner().withUserConfiguration(BeanProvider.class)
                                      .withBean(ScriptExecutor.class, () -> mock(ScriptExecutor.class))
                                      .run(context -> RabbitMQFacade.startListening(queue, "rabbitmq/test-handler"));
        assertEquals(logCaptor.getInfoLogs()
                              .get(0),
                "RabbitMQ receiver created for [" + queue + "]");

        RabbitMQFacade.stopListening(queue, "rabbitmq/test-handler");
        assertEquals(logCaptor.getInfoLogs()
                              .get(1),
                "RabbitMQ receiver stopped for [" + queue + "]");
    }
}
