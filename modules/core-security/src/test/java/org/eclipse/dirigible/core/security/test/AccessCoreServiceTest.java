package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.extensions.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.security.AccessCoreService;
import org.eclipse.dirigible.core.security.AccessDefinition;
import org.eclipse.dirigible.core.security.AccessException;
import org.eclipse.dirigible.core.security.RoleDefinition;
import org.junit.Before;
import org.junit.Test;

public class AccessCoreServiceTest extends AbstractGuiceTest {
	
	@Inject
	private AccessCoreService accessCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.accessCoreService = getInjector().getInstance(AccessCoreService.class);
	}
	
	@Test
	public void createRole() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		List<RoleDefinition> list = accessCoreService.getRoles();
		assertEquals(1, list.size());
		RoleDefinition RoleDefinition = list.get(0);
		assertEquals("test_extpoint1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		accessCoreService.removeRole("test_extpoint1");
	}
	
	@Test
	public void getRole() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		RoleDefinition RoleDefinition = accessCoreService.getRole("test_extpoint1");
		assertEquals("test_extpoint1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		accessCoreService.removeRole("test_extpoint1");
	}
	
	@Test
	public void updatetRole() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		RoleDefinition RoleDefinition = accessCoreService.getRole("test_extpoint1");
		assertEquals("test_extpoint1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		accessCoreService.updateRole("test_extpoint1", "Test 2");
		RoleDefinition = accessCoreService.getRole("test_extpoint1");
		assertEquals("test_extpoint1", RoleDefinition.getName());
		assertEquals("Test 2", RoleDefinition.getDescription());
		accessCoreService.removeRole("test_extpoint1");
	}
	
	@Test
	public void removeRole() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		RoleDefinition RoleDefinition = accessCoreService.getRole("test_extpoint1");
		assertEquals("test_extpoint1", RoleDefinition.getName());
		assertEquals("Test", RoleDefinition.getDescription());
		accessCoreService.removeRole("test_extpoint1");
		RoleDefinition = accessCoreService.getRole("test_extpoint1");
		assertNull(RoleDefinition);
	}
	
	
	
	
	
	
	
	
	@Test
	public void createAccess() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.createAccess("test_ext1", "test_extpoint1", "Test");
		List<AccessDefinition> list = accessCoreService.getAccessDefinitions();
		assertEquals(1, list.size());
		AccessDefinition AccessDefinition = list.get(0);
		assertEquals("test_ext1", AccessDefinition.getLocation());
		assertEquals("test_extpoint1", AccessDefinition.getRole());
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.removeRole("test_extpoint1");
	}
	
	@Test
	public void getAccess() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");

		accessCoreService.removeAccess("test_ext1");
		accessCoreService.createAccess("test_ext1", "test_extpoint1", "Test Ext");
		AccessDefinition AccessDefinition = accessCoreService.getAccess("test_ext1");
		assertEquals("test_ext1", AccessDefinition.getLocation());
		assertEquals("test_extpoint1", AccessDefinition.getRole());
		assertEquals("Test Ext", AccessDefinition.getDescription());
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.removeRole("test_extpoint1");
		
	}
	
	@Test
	public void getExtensions() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		accessCoreService.removeRole("test_extpoint2");
		accessCoreService.createRole("test_extpoint2", "Test");

		accessCoreService.removeAccess("test_ext1");
		accessCoreService.createAccess("test_ext1", "test_extpoint1", "Test Ext 1");
		accessCoreService.removeAccess("test_ext11");
		accessCoreService.createAccess("test_ext11", "test_extpoint1", "Test Ext 11");
		accessCoreService.removeAccess("test_ext2");
		accessCoreService.createAccess("test_ext2", "test_extpoint2", "Test Ext 2");
		accessCoreService.removeAccess("test_ext22");
		accessCoreService.createAccess("test_ext22", "test_extpoint2", "Test Ext 22");
		
		List<AccessDefinition> list = accessCoreService.getAccessDefinitions();
		assertEquals(4, list.size());
		AccessDefinition AccessDefinition = list.get(0);
		assertEquals("test_extpoint1", AccessDefinition.getRole());
		assertEquals("Test Ext 1", AccessDefinition.getDescription());
		
		list = accessCoreService.getAccessByLocation("test_extpoint1");
		assertEquals(2, list.size());
		list = accessCoreService.getAccessByLocation("test_extpoint2");
		assertEquals(2, list.size());
		AccessDefinition = list.get(0);
		assertEquals("test_extpoint2", AccessDefinition.getRole());
		assertEquals("Test Ext 2", AccessDefinition.getDescription());
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.removeAccess("test_ext11");
		accessCoreService.removeAccess("test_ext2");
		accessCoreService.removeAccess("test_ext22");
		
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.removeRole("test_extpoint2");
	}
	
	@Test
	public void updatetAccess() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.createAccess("test_ext1", "test_extpoint1", "Test Ext");
		AccessDefinition AccessDefinition = accessCoreService.getAccess("test_ext1");
		
		assertEquals("test_ext1", AccessDefinition.getLocation());
		assertEquals("test_extpoint1", AccessDefinition.getRole());
		assertEquals("Test Ext", AccessDefinition.getDescription());
		accessCoreService.updateAccess("test_ext1", "test_extpoint1", "role1", "Test Ext 2");
		AccessDefinition = accessCoreService.getAccess("test_ext1");
		assertEquals("test_ext1", AccessDefinition.getLocation());
		assertEquals("Test Ext 2", AccessDefinition.getDescription());
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.removeRole("test_extpoint1");
	}
	
	@Test
	public void removeAccess() throws AccessException {
		accessCoreService.removeRole("test_extpoint1");
		accessCoreService.createRole("test_extpoint1", "Test");
		
		accessCoreService.removeAccess("test_ext1");
		accessCoreService.createAccess("test_ext1", "test_extpoint1", "Test Ext");
		AccessDefinition AccessDefinition = accessCoreService.getAccess("test_ext1");
		accessCoreService.removeAccess("test_ext1");
		AccessDefinition = accessCoreService.getAccess("test_ext1");
		assertNull(AccessDefinition);
		
		accessCoreService.removeRole("test_extpoint1");
	}

}
