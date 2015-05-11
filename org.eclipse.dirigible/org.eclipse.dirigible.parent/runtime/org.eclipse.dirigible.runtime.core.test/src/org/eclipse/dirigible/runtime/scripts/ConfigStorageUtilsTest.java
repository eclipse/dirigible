/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.dirigible.runtime.scripting.AbstractStorageUtils;
import org.eclipse.dirigible.runtime.scripting.utils.ConfigStorageUtils;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;

public class ConfigStorageUtilsTest {

	private static final String PATH = "/a/b/c";
	private static final String KEY = "Key";
	private static final String VALUE1 = "Value1";
	private static final String VALUE2 = "Value2";

	private static Properties properties;
	private static Properties otherProperties;
	private static Properties tooBigProperties;

	private ConfigStorageUtils configStorage;

	@BeforeClass
	public static void initialize() throws Exception {
		properties = new Properties();
		properties.put(KEY, VALUE1);

		tooBigProperties = new Properties();
		tooBigProperties.put(KEY, new String(
				new byte[AbstractStorageUtils.MAX_STORAGE_FILE_SIZE_IN_BYTES + 1]));

		otherProperties = new Properties();
		otherProperties.put(KEY, VALUE2);
	}

	@Before
	public void setUp() throws Exception {
		configStorage = new ConfigStorageUtils(DataSourceUtils.createLocal());
	}

	@After
	public void tearDown() throws Exception {
		configStorage.clear();
	}

	@Test
	public void testPutProperties() throws Exception {
		configStorage.putProperties(PATH, properties);
		assertEquals(properties, configStorage.getProperties(PATH));
	}

	@Test
	public void testPutProperty() throws Exception {
		configStorage.putProperties(PATH, properties);
		configStorage.putProperty(PATH, KEY, VALUE2);
		assertEquals(VALUE2, configStorage.getProperty(PATH, KEY));
	}
	
	@Test
	public void testPutPropertyNotInStorage() throws Exception {
		configStorage.putProperty(PATH, KEY, VALUE1);
		assertEquals(VALUE1, configStorage.getProperty(PATH, KEY));
		Properties retrieved = configStorage.getProperties(PATH);
		assertTrue(retrieved.containsKey(KEY));
		assertTrue(retrieved.containsValue(VALUE1));
	}

	@Test
	public void testGetProperty() throws Exception {
		configStorage.putProperties(PATH, properties);
		assertEquals(VALUE1, configStorage.getProperty(PATH, KEY));
	}

	@Test
	public void testGetPropertyFromNotPersistedProperties() throws Exception {
		try {
			configStorage.getProperty(PATH, KEY);
			fail("Test shoud fail, because " + ConfigStorageUtils.NO_PROPERTY_FOUND_ON_PATH);
		} catch (InvalidParameterException e) {
			assertTrue(e.getMessage().startsWith(ConfigStorageUtils.NO_PROPERTY_FOUND_ON_PATH));
		}
	}

	@Test
	public void testPutTooBigProperties() throws Exception {
		try {
			configStorage.putProperties(PATH, tooBigProperties);
			fail("Test should fail, because " + AbstractStorageUtils.TOO_BIG_DATA_MESSAGE);
		} catch (InvalidParameterException e) {
			assertEquals(AbstractStorageUtils.TOO_BIG_DATA_MESSAGE, e.getMessage());
		}
	}

	@Test
	public void testClear() throws Exception {
		configStorage.putProperties(PATH, properties);
		configStorage.clear();
		assertNull(configStorage.getProperties(PATH));
	}

	@Test
	public void testDelete() throws Exception {
		configStorage.putProperties(PATH, properties);
		configStorage.delete(PATH);
		assertNull(configStorage.getProperties(PATH));
	}

	@Test
	public void testSet() throws Exception {
		configStorage.putProperties(PATH, properties);
		configStorage.putProperties(PATH, otherProperties);
		assertEquals(otherProperties, configStorage.getProperties(PATH));
	}
}
