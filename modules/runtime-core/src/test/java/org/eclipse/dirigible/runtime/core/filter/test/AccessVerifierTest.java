/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.core.filter.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;

import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.runtime.core.filter.AccessVerifier;
import org.junit.Before;
import org.junit.Test;

public class AccessVerifierTest extends AbstractGuiceTest {
	
	@Inject
	private ISecurityCoreService securityCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
	}
	
	@Test
	public void createAccessDefinition() throws AccessException, ServletException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.removeRole("test_role2");
		securityCoreService.removeRole("test_role3");
		securityCoreService.createRole("test_role1", "/abc/my.roles", "Test 1");
		securityCoreService.createRole("test_role2", "/abc/my.roles", "Test 2");
		securityCoreService.createRole("test_role3", "/abc/my.roles", "Test 3");
		
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/d", "GET", "test_role1", "Test");
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/d", "GET", "test_role2", "Test");
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/d/", "GET", "test_role1", "Test");
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/d/", "*", "test_role2", "Test");
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/d/e/f", "*", "test_role3", "Test");
		securityCoreService.createAccessDefinition("/abc/my.access", "/a/b/c/x", "*", "test_role3", "Test");
		
		List<AccessDefinition> matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b", "GET");
		assertTrue(matchingAccessDefinitions.isEmpty());
		
		matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b/c/d", "GET");
		assertFalse(matchingAccessDefinitions.isEmpty());
		assertEquals(2, matchingAccessDefinitions.size());
		
		matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b/c/d/", "GET");
		assertFalse(matchingAccessDefinitions.isEmpty());
		assertEquals(2, matchingAccessDefinitions.size());
		
		matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b/c/d/e", "GET");
		assertFalse(matchingAccessDefinitions.isEmpty());
		assertEquals(2, matchingAccessDefinitions.size());
		
		matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b/c/d/e/f", "GET");
		assertFalse(matchingAccessDefinitions.isEmpty());
		assertEquals(1, matchingAccessDefinitions.size());
		
		matchingAccessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, "/a/b/c/x", "GET");
		assertFalse(matchingAccessDefinitions.isEmpty());
		assertEquals(1, matchingAccessDefinitions.size());
		
		List<AccessDefinition> accessDefinitions = securityCoreService.getAccessDefinitions();
		for (AccessDefinition accessDefinition : accessDefinitions) {
			securityCoreService.removeAccessDefinition(accessDefinition.getId());
		}
		securityCoreService.removeRole("test_role1");
		securityCoreService.removeRole("test_role2");
		securityCoreService.removeRole("test_role3");
	}

}
