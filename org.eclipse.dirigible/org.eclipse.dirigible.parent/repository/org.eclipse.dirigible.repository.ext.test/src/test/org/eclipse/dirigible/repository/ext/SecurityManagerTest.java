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

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.security.SecurityManager;

public class SecurityManagerTest {

	private static DataSource dataSource;
	
	private static IRepository repository;
	
	private static SecurityManager securityManager;
	

	@Before
	public void setUp() {
		DBRepositoryTest.setUp();
		dataSource = DBRepositoryTest.getDataSource();
		repository = DBRepositoryTest.getRepository();
		securityManager = SecurityManager.getInstance(repository, dataSource);
	}

	@Test
	public void testSecureLocation() {
		try {
			securityManager.secureLocation("/location1", null);
			assertTrue(securityManager.isSecuredLocation("/location1"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testUnsecureLocation() {
		try {
			securityManager.secureLocation("/location1", null);
			assertTrue(securityManager.isSecuredLocation("/location1"));
			securityManager.unsecureLocation("/location1");
			assertFalse(securityManager.isSecuredLocation("/location1"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	

}
