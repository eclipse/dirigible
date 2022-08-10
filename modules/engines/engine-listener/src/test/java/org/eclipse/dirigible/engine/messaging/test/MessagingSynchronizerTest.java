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
package org.eclipse.dirigible.engine.messaging.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.messaging.service.MessagingProducer;
import org.eclipse.dirigible.core.messaging.service.SchedulerManager;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.messaging.synchronizer.MessagingSynchronizer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class MessagingSynchronizerTest.
 */
public class MessagingSynchronizerTest extends AbstractDirigibleTest {

	/** The messaging core service. */
	private IMessagingCoreService messagingCoreService;

	/** The messaging synchronizer. */
	private MessagingSynchronizer messagingSynchronizer;

	/** The messaging manager. */
	private SchedulerManager messagingManager;

	/** The repository. */
	private IRepository repository;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.messagingCoreService = new MessagingCoreService();
		this.messagingSynchronizer = new MessagingSynchronizer();
		this.messagingManager = new SchedulerManager();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

		this.messagingManager.initialize();
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		SchedulerManager.shutdown();
	}

	/**
	 * Full listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void fullListenerTest() throws MessagingException, IOException, InterruptedException {
		createListenerTest();

		Thread.sleep(2000);

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.listener");

		messagingSynchronizer.synchronize();

		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/custom/custom.listener");
		assertNull(listenerDefinition);
		assertFalse(messagingManager.existsListener("/custom/custom.listener"));

		Thread.sleep(1000);
	}

	/**
	 * Creates the listener test.
	 *
	 * @throws MessagingException
	 *             the messaging exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createListenerTest() throws MessagingException, IOException {
		messagingSynchronizer.registerPredeliveredListener("/control/control.listener");

		ListenerDefinition listenerDefinitionCustom = new ListenerDefinition();
		listenerDefinitionCustom.setLocation("/custom/custom.listener");
		listenerDefinitionCustom.setName("/custom/custom");
		listenerDefinitionCustom.setType(IMessagingCoreService.TOPIC);
		listenerDefinitionCustom.setHandler("custom/custom");
		listenerDefinitionCustom.setDescription("Test");
		listenerDefinitionCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		listenerDefinitionCustom.setCreatedBy("test_user");

		String json = messagingCoreService.serializeListener(listenerDefinitionCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.listener", json.getBytes());

		messagingSynchronizer.synchronize();

		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/control/control.listener");
		assertNotNull(listenerDefinition);
		listenerDefinition = messagingCoreService.getListener("/custom/custom.listener");
		assertNotNull(listenerDefinition);

		MessagingProducer messagingProducer = new MessagingProducer("/control/control", IMessagingCoreService.QUEUE, "Test Message");
		new Thread(messagingProducer).start();
	}

}
