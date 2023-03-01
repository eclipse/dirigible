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
package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessArtifactConstraint;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.junit.Test;

/**
 * The Class AccessArtifactTest.
 */
public class AccessArtifactTest {
	
	/**
	 * Serialize test.
	 */
	@Test
	public void serializeTest() {
		AccessArtifact access = new AccessArtifact();
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(0).setPath("/myproject/myfolder/myartifact1.txt");
		access.getConstraints().get(0).setMethod("*");
		access.getConstraints().get(0).getRoles().add("myrole1");
		access.getConstraints().get(0).getRoles().add("myrole2");
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(1).setPath("/myproject/myfolder/myartifact2.txt");
		access.getConstraints().get(1).setMethod("GET");
		access.getConstraints().get(1).getRoles().add("myrole3");
		access.getConstraints().get(1).getRoles().add("myrole4");
		assertNotNull(access.serialize());
	}

	/**
	 * Parses the test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void parseTest() throws IOException {
		InputStream in = AccessArtifactTest.class.getResourceAsStream("/META-INF/dirigible/access/test.access");
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			AccessArtifact access = AccessArtifact.parse(json);
			assertEquals("*", access.getConstraints().get(0).getMethod());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * Combine test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void combineTest() throws IOException {
		List<AccessDefinition> accessDefinitions = new ArrayList<AccessDefinition>();
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setPath("/myproject/myfolder/myartifact1.txt");
		accessDefinition.setMethod("*");
		accessDefinition.setRole("myrole1");
		accessDefinitions.add(accessDefinition);
		accessDefinition.setHash("1234");
		accessDefinition = new AccessDefinition();
		accessDefinition.setPath("/myproject/myfolder/myartifact1.txt");
		accessDefinition.setMethod("*");
		accessDefinition.setRole("myrole2");
		accessDefinition.setHash("1234");
		accessDefinitions.add(accessDefinition);
		accessDefinition = new AccessDefinition();
		accessDefinition.setPath("/myproject/myfolder/myartifact2.txt");
		accessDefinition.setMethod("GET");
		accessDefinition.setRole("myrole3");
		accessDefinition.setHash("1234");
		accessDefinitions.add(accessDefinition);
		accessDefinition = new AccessDefinition();
		accessDefinition.setPath("/myproject/myfolder/myartifact2.txt");
		accessDefinition.setMethod("GET");
		accessDefinition.setRole("myrole4");
		accessDefinition.setHash("1234");
		accessDefinitions.add(accessDefinition);
		AccessArtifact access = AccessArtifact.combine(accessDefinitions);
		assertEquals("*", access.getConstraints().get(0).getMethod());
	}
	
	/**
	 * Divide test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void divideTest() throws IOException {
		String json = IOUtils.toString(AccessArtifactTest.class.getResourceAsStream("/META-INF/dirigible/access/test.access"), StandardCharsets.UTF_8);
		AccessArtifact access = AccessArtifact.parse(json);
		List<AccessDefinition> accessDefinitions = access.divide();
		assertEquals("*", accessDefinitions.get(1).getMethod());
	}
}
