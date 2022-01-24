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
package org.eclipse.dirigible.commons.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * The Class ConfigurationTest.
 */
public class ConfigurationTest {

	/**
	 * Inits the test.
	 */
	@Test
	public void initTest() {
		String value = Configuration.get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("local", value);
	}

	/**
	 * Update test.
	 */
	@Test
	public void updateTest() {
		String value = Configuration.get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("local", value);

		System.setProperty("DIRIGIBLE_INSTANCE_NAME", "test");

		Configuration.update();

		value = Configuration.get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("test", value);

		System.setProperty("DIRIGIBLE_INSTANCE_NAME", "local");

		Configuration.update();
	}

	/**
	 * Custom test.
	 */
	@Test
	public void customTest() {
		Configuration.loadModuleConfig("/test.properties");
		String value = Configuration.get("DIRIGIBLE_TEST_PROPERTY");
		assertEquals("test", value);
	}

	// Expected Precedence:
	// 1. Runtime defined properties
	// 2. System properties (-D...)
	// 3. Environment properties
	// 4. Deployment properties
	// 5. Module loaded *.properties files
	@Test
	public void configPrecedenceTest() {
		String property = "PATH";

		String envProperty = System.getenv(property);
		assertNotNull(envProperty);

		String value = Configuration.get(property);
		assertNotNull(value);
		assertEquals(envProperty, value);

		Configuration.loadModuleConfig("/precedence.properties");
		value = Configuration.get(property);
		assertNotNull(value);
		assertNotEquals("/my/path", value);
		assertEquals(envProperty, value);

		Configuration.setSystemProperty(property, "/my/new/path");
		Configuration.update();
		value = Configuration.get(property);
		assertEquals("/my/new/path", value);

		Configuration.set(property, "/my/runtime/path");
		value = Configuration.get(property);
		assertEquals("/my/runtime/path", value);
	}

	@Test
	public void configPrecedenceDeploymentTest() {
		String property = "DIRIGIBLE_PRODUCT_TYPE";

		String envProperty = System.getenv(property);
		assertNull(envProperty);

		String value = Configuration.get(property);
		assertNotNull(value);
		assertEquals("all", value);

		Configuration.setSystemProperty(property, "all-test");
		Configuration.update();
		value = Configuration.get(property);
		assertNotNull(value);
		assertEquals("all-test", value);

		Configuration.set(property, "all-test-updated");
		value = Configuration.get(property);
		assertNotNull(value);
		assertEquals("all-test-updated", value);
	}

	@Test
	public void configPrecedenceNoEnvTest() {
		String property = "TEST_PROPERTY_USERNAME";

		String envProperty = System.getenv(property);
		assertNull(envProperty);

		String value = Configuration.get(property);
		assertNull(value);

		Configuration.loadModuleConfig("/precedence-no-env.properties");
		value = Configuration.get(property);
		assertNotNull(value);
		assertEquals("test", value);

		Configuration.setSystemProperty(property, "DIRIGIBLE_USERNAME");
		Configuration.update();
		value = Configuration.get(property);
		assertEquals("DIRIGIBLE_USERNAME", value);

		Configuration.set(property, "DIRIGIBLE_USERNAME_NEW");
		value = Configuration.get(property);
		assertEquals("DIRIGIBLE_USERNAME_NEW", value);
	}
}
