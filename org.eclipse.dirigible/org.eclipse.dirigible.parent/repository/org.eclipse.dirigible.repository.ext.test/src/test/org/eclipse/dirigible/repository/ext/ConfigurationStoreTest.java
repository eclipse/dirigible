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
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.conf.ConfigurationStore;
import org.eclipse.dirigible.repository.ext.conf.IConfigurationStore;

public class ConfigurationStoreTest {

	private static IRepository repository;
	
	@Before
	public void setUp() {
		DBRepositoryTest.setUp();
		repository = DBRepositoryTest.getRepository();
	}

	@Test
	public void testcopyCollectionToDirectory() {
		try {
			IConfigurationStore configurationStorage = 
					new ConfigurationStore(repository);
			
			Properties properties = new Properties();
			properties.put("property1", "value1");
			properties.put("property2", "value2");
			configurationStorage.setGlobalSettings("/myConfig", "special_settings", properties);

			String path = IRepositoryPaths.CONF_REGISTRY + "/myConfig/special_settings.properties";
			assertTrue(repository.hasResource(path));
			IResource resource = repository.getResource(path);
			
			Properties retrieved = new Properties();
			retrieved.load(new ByteArrayInputStream(resource.getContent()));
			
			String var1 = retrieved.getProperty("property1");
			String var2 = retrieved.getProperty("property2");
			
			assertEquals("value1", var1);
			assertEquals("value2", var2);
			
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}
		
}
