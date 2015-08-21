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

package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionDefinition;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionManager;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionPointDefinition;

public class ExtensionManagerTest {

	private static DataSource dataSource;
	
	private static IRepository repository;
	
	private static ExtensionManager extensionManager;
	

	@Before
	public void setUp() {
		DBRepositoryTest.setUp();
		dataSource = DBRepositoryTest.getDataSource();
		repository = DBRepositoryTest.getRepository();
		extensionManager = new ExtensionManager(repository, dataSource, null);
	}

	@Test
	public void testCreateExtensionPoint() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			String[] extensionPoints = extensionManager.getExtensionPoints();
			assertTrue(contains(extensionPoints,"extensionPoint1"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	private boolean contains(String[] array, String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[0] != null
					&& array[0].equals(string)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testCreateExtension() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			extensionManager.createExtension("extension1", "extensionPoint1", "test extension");
			String[] extensions = extensionManager.getExtensions("extensionPoint1");
			assertTrue(contains(extensions,"extension1"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRemoveExtension() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			extensionManager.createExtension("extension1", "extensionPoint1", "test extension");
			String[] extensions = extensionManager.getExtensions("extensionPoint1");
			assertTrue(contains(extensions,"extension1"));
			
			extensionManager.removeExtension("extension1", "extensionPoint1");
			extensions = extensionManager.getExtensions("extensionPoint1");
			assertFalse(contains(extensions,"extension1"));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testGetExtensionPoint() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			ExtensionPointDefinition extensionPointDefinition = extensionManager.getExtensionPoint("extensionPoint1");
			assertNotNull(extensionPointDefinition);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetExtension() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			extensionManager.createExtension("extension1", "extensionPoint1", "test extension");
			ExtensionDefinition extensionDefinition = extensionManager.getExtension("extension1", "extensionPoint1");
			assertNotNull(extensionDefinition);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateExtensionPoint() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			ExtensionPointDefinition extensionPointDefinition = extensionManager.getExtensionPoint("extensionPoint1");
			assertNotNull(extensionPointDefinition);
			assertEquals("test extension point", extensionPointDefinition.getDescription());
			extensionManager.updateExtensionPoint("extensionPoint1", "test extension point updated");
			extensionPointDefinition = extensionManager.getExtensionPoint("extensionPoint1");
			assertEquals("test extension point updated", extensionPointDefinition.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateExtension() {
		try {
			extensionManager.removeExtensionPoint("extensionPoint1");
			extensionManager.createExtensionPoint("extensionPoint1", "test extension point");
			extensionManager.createExtension("extension1", "extensionPoint1", "test extension");
			ExtensionDefinition extensionDefinition = extensionManager.getExtension("extension1", "extensionPoint1");
			assertNotNull(extensionDefinition);
			assertEquals("test extension", extensionDefinition.getDescription());
			extensionManager.updateExtension("extension1", "extensionPoint1", "test extension updated");
			extensionDefinition = extensionManager.getExtension("extension1", "extensionPoint1");
			assertEquals("test extension updated", extensionDefinition.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
