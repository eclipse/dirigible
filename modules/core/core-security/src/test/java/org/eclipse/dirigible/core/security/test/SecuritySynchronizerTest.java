/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessArtifactConstraint;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.security.synchronizer.SecuritySynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class SecuritySynchronizerTest.
 */
public class SecuritySynchronizerTest extends AbstractGuiceTest {

	/** The security core service. */
	@Inject
	private ISecurityCoreService securityCoreService;

	/** The security publisher. */
	@Inject
	private SecuritySynchronizer securityPublisher;

	/** The repository. */
	@Inject
	private IRepository repository;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
		this.securityPublisher = getInjector().getInstance(SecuritySynchronizer.class);
		this.repository = getInjector().getInstance(IRepository.class);
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
		securityPublisher.registerPredeliveredAccess("/access/test.access");

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

		securityPublisher.synchronize();

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

		securityPublisher.synchronize();

		AccessDefinition accessDefinition = securityCoreService.getAccessDefinition("HTTP", "/myproject/myfolder/myartifact3.txt", "GET", "myrole1");
		assertNull(accessDefinition);
		assertTrue(securityCoreService.isAccessAllowed("HTTP", "/myproject/myfolder/myartifact3.txt", "GET", "myrole1"));
	}

}
