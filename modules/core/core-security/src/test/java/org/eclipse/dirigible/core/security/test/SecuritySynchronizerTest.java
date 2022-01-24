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
package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessArtifactConstraint;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.security.synchronizer.SecuritySynchronizer;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class SecuritySynchronizerTest.
 */
public class SecuritySynchronizerTest extends AbstractDirigibleTest {

	/** The security core service. */
	private ISecurityCoreService securityCoreService;

	/** The security publisher. */
	private SecuritySynchronizer securitySynchronizer;

	/** The repository. */
	private IRepository repository;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = new SecurityCoreService();
		this.securitySynchronizer = new SecuritySynchronizer();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
	}

	/**
	 * Creates the access test.
	 *
	 * @throws SecurityException the security exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AccessException the access exception
	 */
	@Test
	public void createAccessTest() throws SecurityException, IOException, AccessException {
		securitySynchronizer.registerPredeliveredAccess("/access/test.access");

		AccessArtifact access = new AccessArtifact();
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(0).setPath("/myproject/myfolder/myartifact3.txt");
		access.getConstraints().get(0).setMethod("*");
		access.getConstraints().get(0).getRoles().add("myrole1");
		access.getConstraints().get(0).getRoles().add("myrole2");
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(1).setPath("/myproject/myfolder/myartifact4.txt");
		access.getConstraints().get(1).setMethod("GET");
		access.getConstraints().get(1).getRoles().add("myrole3");
		access.getConstraints().get(1).getRoles().add("myrole4");
		String json = access.serialize();

		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/access/test.access", json.getBytes());

		securitySynchronizer.synchronize();

		AccessDefinition accessDefinition = securityCoreService.getAccessDefinition("HTTP", "/myproject/myfolder/myartifact3.txt", "*", "myrole1");
		assertNotNull(accessDefinition);
		assertTrue(securityCoreService.isAccessAllowed("HTTP", "/myproject/myfolder/myartifact3.txt", "GET", "myrole1"));
	}

	/**
	 * Cleanup access test.
	 *
	 * @throws SecurityException the security exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AccessException the access exception
	 */
	@Test
	public void cleanupAccessTest() throws SecurityException, IOException, AccessException {
		createAccessTest();

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/access/test.access");

		securitySynchronizer.synchronize();

		AccessDefinition accessDefinition = securityCoreService.getAccessDefinition("HTTP", "/myproject/myfolder/myartifact3.txt", "GET", "myrole1");
		assertNull(accessDefinition);
		assertTrue(securityCoreService.isAccessAllowed("HTTP", "/myproject/myfolder/myartifact3.txt", "GET", "myrole1"));
	}

}
