/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.rabbitmq;

import static org.junit.Assert.*;

import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.RabbitMQContainer;

import nl.altindag.log.LogCaptor;

public class RabbitMQFacadeTest {

	RabbitMQFacade facade = new RabbitMQFacade();
	LogCaptor logCaptor = LogCaptor.forClass(RabbitMQFacade.class);
	private static final String message = "testMessage";
	private static final String queue = "test-queue";
	  
	
	@Before
	public void setUp() {
		RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.8.19-alpine");
		rabbit.start();
		
		String host = rabbit.getHost();
	    Integer port = rabbit.getFirstMappedPort();
	    Configuration.set("DIRIGIBLE_RABBITMQ_CLIENT_URI", host + ":" + port);
	}
	
	@Test
	public void send() {
		logCaptor.setLogLevelToInfo();
		
		facade.send(queue, message);
		assertEquals(logCaptor.getInfoLogs().get(0).toString(), "Sent: " + "'" + message + "'" + " to [" + queue + "]");
		
	}
	
	@Test
	public void rabbitMQIntegration() {
		logCaptor.setLogLevelToInfo();
		
		facade.startListening(queue, "rabbitmq/test-handler");
		assertEquals(logCaptor.getInfoLogs().get(0).toString(), "RabbitMQ receiver created for [" + queue + "]");
		
		facade.stopListening(queue, "rabbitmq/test-handler");
		assertEquals(logCaptor.getInfoLogs().get(1).toString(), "RabbitMQ receiver stopped for [" + queue + "]");
	}	
}
