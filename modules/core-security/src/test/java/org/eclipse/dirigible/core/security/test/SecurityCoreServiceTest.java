package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.security.AccessException;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.ISecurityCoreService;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class SecurityCoreServiceTest extends AbstractGuiceTest {
	
	@Inject
	private ISecurityCoreService securityCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
	}
	
	@Test
	public void createRole() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		List<RoleDefinition> list = securityCoreService.getRoles();
		assertEquals(1, list.size());
		RoleDefinition RoleDefinition = list.get(0);
		assertEquals("test_role1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		securityCoreService.removeRole("test_role1");
	}
	
	@Test
	public void getRole() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		RoleDefinition RoleDefinition = securityCoreService.getRole("test_role1");
		assertEquals("test_role1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		securityCoreService.removeRole("test_role1");
	}
	
	@Test
	public void updatetRole() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		RoleDefinition RoleDefinition = securityCoreService.getRole("test_role1");
		assertEquals("test_role1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		securityCoreService.updateRole("test_role1", "Test 2");
		RoleDefinition = securityCoreService.getRole("test_role1");
		assertEquals("test_role1", RoleDefinition.getName());
		assertEquals("Test 2", RoleDefinition.getDescription());
		securityCoreService.removeRole("test_role1");
	}
	
	@Test
	public void removeRole() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		RoleDefinition RoleDefinition = securityCoreService.getRole("test_role1");
		assertEquals("test_role1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		securityCoreService.removeRole("test_role1");
		RoleDefinition = securityCoreService.getRole("test_role1");
		assertNull(RoleDefinition);
	}
	
	
	
	
	
	
	
	
	@Test
	public void createAccessDefinition() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		
		securityCoreService.createAccessDefinition("test_access1", "GET", "test_role1", "Test");
		List<AccessDefinition> list = securityCoreService.getAccessDefinitions();
		assertEquals(1, list.size());
		AccessDefinition accessDefinition = list.get(0);
		assertEquals("test_access1", accessDefinition.getLocation());
		assertEquals("test_role1", accessDefinition.getRole());
		
		securityCoreService.removeAccessDefinition(accessDefinition.getId());
		securityCoreService.removeRole("test_role1");
	}
	
	@Test
	public void getAccessDefinition() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");

		AccessDefinition accessDefinition = securityCoreService.createAccessDefinition("test_access1", "GET", "test_role1", "Test access");
		accessDefinition = securityCoreService.getAccessDefinition(accessDefinition.getId());
		assertEquals("test_access1", accessDefinition.getLocation());
		assertEquals("test_role1", accessDefinition.getRole());
		assertEquals("Test access", accessDefinition.getDescription());
		
		securityCoreService.removeAccessDefinition(accessDefinition.getId());
		securityCoreService.removeRole("test_role1");
		
	}
	
	@Test
	public void getAccessDefinitionList() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		securityCoreService.removeRole("test_role2");
		securityCoreService.createRole("test_role2", "Test");

		securityCoreService.createAccessDefinition("test_access1", "GET", "test_role1", "Test access 1");
		securityCoreService.createAccessDefinition("test_access1", "POST", "test_role11", "Test access 11");
		securityCoreService.createAccessDefinition("test_access2", "PUT", "test_role2", "Test access 2");
		securityCoreService.createAccessDefinition("test_access2", "DELETE", "test_role22", "Test access 22");
		
		List<AccessDefinition> list = securityCoreService.getAccessDefinitions();
		assertEquals(4, list.size());
		AccessDefinition AccessDefinition = list.get(0);
		assertEquals("test_role1", AccessDefinition.getRole());
		assertEquals("Test access 1", AccessDefinition.getDescription());
		
		list = securityCoreService.getAccessDefinitionsByLocation("test_access1");
		assertEquals(2, list.size());
		list = securityCoreService.getAccessDefinitionsByLocation("test_access2");
		assertEquals(2, list.size());
		AccessDefinition = list.get(0);
		assertEquals("test_role2", AccessDefinition.getRole());
		assertEquals("Test access 2", AccessDefinition.getDescription());
		
		list = securityCoreService.getAccessDefinitionsByLocationAndMethod("test_access2", "PUT");
		assertEquals(1, list.size());
		AccessDefinition = list.get(0);
		assertEquals("test_role2", AccessDefinition.getRole());
		assertEquals("Test access 2", AccessDefinition.getDescription());
		
		list = securityCoreService.getAccessDefinitions();
		for (AccessDefinition accessDefinition : list) {
			securityCoreService.removeAccessDefinition(accessDefinition.getId());
		}
		
		securityCoreService.removeRole("test_role1");
		securityCoreService.removeRole("test_role2");
	}
	
	@Test
	public void updatetAccessDefinition() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		
		AccessDefinition accessDefinition = securityCoreService.createAccessDefinition("test_access1", "GET", "test_role1", "Test access");
		accessDefinition = securityCoreService.getAccessDefinition(accessDefinition.getId());
		
		assertEquals("test_access1", accessDefinition.getLocation());
		assertEquals("test_role1", accessDefinition.getRole());
		assertEquals("Test access", accessDefinition.getDescription());
		securityCoreService.updateAccessDefinition(accessDefinition.getId(), "test_access2", "*", "test_role1", "Test access 2");
		accessDefinition = securityCoreService.getAccessDefinition(accessDefinition.getId());
		assertEquals("test_access2", accessDefinition.getLocation());
		assertEquals("Test access 2", accessDefinition.getDescription());
		
		securityCoreService.removeAccessDefinition(accessDefinition.getId());
		securityCoreService.removeRole("test_role1");
	}
	
	@Test
	public void removeAccessDefinition() throws AccessException {
		securityCoreService.removeRole("test_role1");
		securityCoreService.createRole("test_role1", "Test");
		
		AccessDefinition accessDefinition = securityCoreService.createAccessDefinition("test_access1", AccessDefinition.METHOD_ANY, "test_role1", "Test access");
		accessDefinition = securityCoreService.getAccessDefinition(accessDefinition.getId());
		securityCoreService.removeAccessDefinition(accessDefinition.getId());
		accessDefinition = securityCoreService.getAccessDefinition(accessDefinition.getId());
		assertNull(accessDefinition);
		
		securityCoreService.removeRole("test_role1");
	}

}
