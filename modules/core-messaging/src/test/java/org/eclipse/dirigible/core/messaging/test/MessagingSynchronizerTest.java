package org.eclipse.dirigible.core.messaging.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.eclipse.dirigible.core.messaging.api.ListenerType;
import org.eclipse.dirigible.core.messaging.api.MessagingException;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.core.messaging.service.MessagingCoreService;
import org.eclipse.dirigible.core.messaging.synchronizer.MessagingSynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

public class MessagingSynchronizerTest extends AbstractGuiceTest {

	@Inject
	private IMessagingCoreService messagingCoreService;

	@Inject
	private MessagingSynchronizer messagingPublisher;

	@Inject
	private IRepository repository;

	@Before
	public void setUp() throws Exception {
		this.messagingCoreService = getInjector().getInstance(MessagingCoreService.class);
		this.messagingPublisher = getInjector().getInstance(MessagingSynchronizer.class);
		this.repository = getInjector().getInstance(IRepository.class);
	}

	@Test
	public void createListenerTest() throws MessagingException, IOException {
		messagingPublisher.registerPredeliveredListener("/control/control.listener");

		ListenerDefinition listenerDefinitionCustom = new ListenerDefinition();
		listenerDefinitionCustom.setLocation("/custom/custom.listener");
		listenerDefinitionCustom.setName("/custom/custom");
		listenerDefinitionCustom.setType(new Integer(ListenerType.TOPIC.ordinal()).byteValue());
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

	}

	@Test
	public void cleanupListenerTest() throws MessagingException, IOException {
		createListenerTest();

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.listener");

		messagingPublisher.synchronize();

		ListenerDefinition listenerDefinition = messagingCoreService.getListener("/custom/custom.listener");
		assertNull(listenerDefinition);

	}

}
