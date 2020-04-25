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
package org.eclipse.dirigible.commons.config;

import static org.junit.Assert.assertEquals;

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
		Configuration.load("/test.properties");
		String value = Configuration.get("DIRIGIBLE_TEST_PROPERTY");
		assertEquals("test", value);
	}

}
