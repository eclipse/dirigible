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
package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.synchronizer.ExtensionsSynchronizer;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class ExtensionsSynchronizerTest.
 */
public class ExtensionsSynchronizerTest extends AbstractDirigibleTest {

	/** The extensions core service. */
	private IExtensionsCoreService extensionsCoreService;

	/** The extensions publisher. */
	private ExtensionsSynchronizer extensionsPublisher;

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
		this.extensionsCoreService = new ExtensionsCoreService();
		this.extensionsPublisher = new ExtensionsSynchronizer();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
	}

	/**
	 * Creates the extension point test.
	 *
	 * @throws ExtensionsException
	 *             the extensions exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void createExtensionPointTest() throws ExtensionsException, IOException {
		extensionsPublisher.registerPredeliveredExtensionPoint("/control/control.extensionpoint");
		extensionsPublisher.registerPredeliveredExtension("/control/control.extension");

		ExtensionPointDefinition extensionPointDefinitionCustom = new ExtensionPointDefinition();
		extensionPointDefinitionCustom.setLocation("/custom/custom.extensionpoint");
		extensionPointDefinitionCustom.setName("/custom/custom");
		extensionPointDefinitionCustom.setDescription("Test");
		extensionPointDefinitionCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		extensionPointDefinitionCustom.setCreatedBy("test_user");

		String json = extensionsCoreService.serializeExtensionPoint(extensionPointDefinitionCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.extensionpoint", json.getBytes());

		extensionsPublisher.synchronize();

		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPoint("/control/control.extensionpoint");
		assertNotNull(extensionPointDefinition);
		extensionPointDefinition = extensionsCoreService.getExtensionPoint("/custom/custom.extensionpoint");
		assertNotNull(extensionPointDefinition);

	}

	/**
	 * Cleanup extension point test.
	 *
	 * @throws ExtensionsException
	 *             the extensions exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void cleanupExtensionPointTest() throws ExtensionsException, IOException {
		createExtensionPointTest();

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.extensionpoint");

		extensionsPublisher.synchronize();

		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPoint("/custom/custom.extensionpoint");
		assertNull(extensionPointDefinition);

	}

}
