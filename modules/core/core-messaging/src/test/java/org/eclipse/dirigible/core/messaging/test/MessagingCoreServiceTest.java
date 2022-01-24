/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.messaging.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class MessagingCoreServiceTest.
 */
public class MessagingCoreServiceTest extends AbstractDirigibleTest {

	private IMessagingCoreService messagingCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.messagingCoreService = new MessagingCoreService();
	}

	/**
	 * Creates the listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 */
	@Test
	public void createListenerTest() throws MessagingException {
		messagingCoreService.removeListener("/test_listener1");
		messagingCoreService.createListener("/test_listener1", "test_listener1", IMessagingCoreService.QUEUE, "test_handler1", "Test");
		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertEquals("test_listener1", listenerDefinition.getName());
		assertEquals("Test", listenerDefinition.getDescription());
		messagingCoreService.removeListener("/test_listener1");
	}

	/**
	 * Gets the listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 */
	@Test
	public void getListenerTest() throws MessagingException {
		messagingCoreService.removeListener("/test_listener1");
		messagingCoreService.createListener("/test_listener1", "test_listener1", IMessagingCoreService.QUEUE, "test_handler1", "Test");
		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertEquals("test_listener1", listenerDefinition.getName());
		assertEquals("Test", listenerDefinition.getDescription());
		messagingCoreService.removeListener("/test_listener1");
	}

	/**
	 * Updatet listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 */
	@Test
	public void updatetListenerTest() throws MessagingException {
		messagingCoreService.removeListener("/test_listener1");
		messagingCoreService.createListener("/test_listener1", "test_listener1", IMessagingCoreService.QUEUE, "test_handler1", "Test");
		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertEquals("test_listener1", listenerDefinition.getName());
		assertEquals("Test", listenerDefinition.getDescription());
		messagingCoreService.updateListener("/test_listener1", "test_listener1", IMessagingCoreService.QUEUE, "test_handler1", "Test 2");
		listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertEquals("test_listener1", listenerDefinition.getName());
		assertEquals("Test 2", listenerDefinition.getDescription());
		messagingCoreService.removeListener("/test_listener1");
	}

	/**
	 * Removes the listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 */
	@Test
	public void removeListenerTest() throws MessagingException {
		messagingCoreService.removeListener("/test_listener1");
		messagingCoreService.createListener("/test_listener1", "test_listener1", IMessagingCoreService.QUEUE, "test_handler1", "Test");
		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertEquals("test_listener1", listenerDefinition.getName());
		assertEquals("Test", listenerDefinition.getDescription());
		messagingCoreService.removeListener("/test_listener1");
		listenerDefinition = messagingCoreService.getListener("/test_listener1");
		assertNull(listenerDefinition);
	}

	/**
	 * Parses the listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 */
	@Test
	public void parseListenerTest() throws MessagingException {
		ListenerDefinition listenerDefinition = new ListenerDefinition();
		listenerDefinition.setLocation("/test_listener1");
		listenerDefinition.setName("test_listener1");
		listenerDefinition.setDescription("Test");
		listenerDefinition.setCreatedAt(new Timestamp(new Date().getTime()));
		listenerDefinition.setCreatedBy("test_user");
		String json = messagingCoreService.serializeListener(listenerDefinition);
		System.out.println(json);
		ListenerDefinition listenerDefinition2 = messagingCoreService.parseListener(json);
		assertEquals(listenerDefinition.getName(), listenerDefinition2.getName());
	}

}
