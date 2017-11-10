/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.messaging.service.MessagingManager;
import org.eclipse.dirigible.core.messaging.service.MessagingProducer;
import org.eclipse.dirigible.core.messaging.synchronizer.MessagingSynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessagingSynchronizerTest extends AbstractGuiceTest {

	@Inject
	private IMessagingCoreService messagingCoreService;

	@Inject
	private MessagingSynchronizer messagingPublisher;

	@Inject
	private MessagingManager messagingManager;

	@Inject
	private IRepository repository;

	@Before
	public void setUp() throws Exception {
		this.messagingCoreService = getInjector().getInstance(MessagingCoreService.class);
		this.messagingPublisher = getInjector().getInstance(MessagingSynchronizer.class);
		this.messagingManager = getInjector().getInstance(MessagingManager.class);
		this.repository = getInjector().getInstance(IRepository.class);

		messagingManager.initialize();
	}

	@After
	public void tearDown() throws Exception {
		messagingManager.shutdown();
	}

	@Test
	public void fullListenerTest() throws MessagingException, IOException, InterruptedException {
		createListenerTest();

		Thread.sleep(2000);

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.listener");

		messagingPublisher.synchronize();

		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/custom/custom.listener");
		assertNull(listenerDefinition);
		assertFalse(messagingManager.existsListener("/custom/custom.listener"));

		Thread.sleep(1000);
	}

	public void createListenerTest() throws MessagingException, IOException {
		messagingPublisher.registerPredeliveredListener("/control/control.listener");

		ListenerDefinition listenerDefinitionCustom = new ListenerDefinition();
		listenerDefinitionCustom.setLocation("/custom/custom.listener");
		listenerDefinitionCustom.setName("/custom/custom");
		listenerDefinitionCustom.setType(new Integer(DestinationType.TOPIC.ordinal()).byteValue());
		listenerDefinitionCustom.setModule("custom/custom");
		listenerDefinitionCustom.setDescription("Test");
		listenerDefinitionCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		listenerDefinitionCustom.setCreatedBy("test_user");

		String json = messagingCoreService.serializeListener(listenerDefinitionCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.listener", json.getBytes());

		messagingPublisher.synchronize();

		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/control/control.listener");
		assertNotNull(listenerDefinition);
		listenerDefinition = messagingCoreService.getListener("/custom/custom.listener");
		assertNotNull(listenerDefinition);

		MessagingProducer messagingProducer = new MessagingProducer("/control/control", DestinationType.QUEUE, "Test Message");
		new Thread(messagingProducer).start();
	}

}
