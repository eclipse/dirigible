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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class RolesArtifactTest.
 */
public class RolesArtifactTest extends AbstractGuiceTest {
	
	/** The security core service. */
	@Inject
	private ISecurityCoreService securityCoreService;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
	}
	
	/**
	 * Serialize test.
	 */
	@Test
	public void serializeTest() {
		RoleDefinition[] roles = new RoleDefinition[2];
		roles[0] = new RoleDefinition();
		roles[0].setName("myrole1");
		roles[0].setDescription("Role1 Description");
		roles[1] = new RoleDefinition();
		roles[1].setName("myrole2");
		roles[1].setDescription("Role2 Description");
		assertNotNull(securityCoreService.serializeRoles(roles));
	}

	/**
	 * Parses the test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void parseTest() throws IOException {
		InputStream in = RolesArtifactTest.class.getResourceAsStream("/access/test.roles");
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			RoleDefinition[] roles = securityCoreService.parseRoles(json);
			assertEquals("myrole2", roles[1].getName());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	
}
