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
package org.eclipse.dirigible.core.registry.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.dirigible.commons.api.helpers.FileSystemUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.registry.synchronizer.RegistrySynchronizer;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class RegistrySynchronizerTest.
 */
public class RegistrySynchronizerTest extends AbstractDirigibleTest {

	/** The Constant DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER. */
	private static final String DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER = System.getProperty("user.dir") + "/target";

	/** The synchronizer. */
	private RegistrySynchronizer synchronizer;

	/** The repository. */
	private IRepository repository;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.synchronizer = new RegistrySynchronizer();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

		System.setProperty("DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER", DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER);
		Configuration.update();
	}

	/**
	 * Synchronize registry test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void synchronizeRegistryTest() throws Exception {
		String expectedText = "My Data";
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/user1/workspace1/project1/folder1/file.txt", expectedText.getBytes());
		synchronizer.synchronize();
		
		byte[] fileContent = FileSystemUtils.loadFile(
				DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER
				+ IRepositoryStructure.PATH_REGISTRY_PUBLIC
				+ "/user1/workspace1/project1/folder1/file.txt");

		String actualText = new String(fileContent);
		assertEquals(expectedText, actualText);
	}
}
