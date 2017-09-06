package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.DataStructureTableModel;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class ExtensionsCoreServiceTest extends AbstractGuiceTest {
	
	@Inject
	private IExtensionsCoreService extensionsCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.extensionsCoreService = getInjector().getInstance(ExtensionsCoreService.class);
	}
	
	@Test
	public void createExtensionPointTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		DataStructureTableModel extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
	}
	
	@Test
	public void getExtensionPointTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		DataStructureTableModel extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
	}
	
	@Test
	public void updatetExtensionPointTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		DataStructureTableModel extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.updateExtensionPoint("/test_extpoint1", "test_extpoint1", "Test 2");
		extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getName());
		assertEquals("Test 2", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
	}
	
	@Test
	public void removeExtensionPointTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		DataStructureTableModel extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionPointDefinition = extensionsCoreService.getExtensionPoint("/test_extpoint1");
		assertNull(extensionPointDefinition);
	}
	
	@Test
	public void parseExtensionPointTest() throws ExtensionsException {
		DataStructureTableModel extensionPointDefinition = new DataStructureTableModel();
		extensionPointDefinition.setLocation("/test_extpoint1");
		extensionPointDefinition.setName("test_extpoint1");
		extensionPointDefinition.setDescription("Test");
		extensionPointDefinition.setCreatedAt(new Timestamp(new Date().getTime()));
		extensionPointDefinition.setCreatedBy("test_user");
		String json = extensionsCoreService.serializeExtensionPoint(extensionPointDefinition);
		System.out.println(json);
		DataStructureTableModel extensionPointDefinition2 = extensionsCoreService.parseExtensionPoint(json);
		assertEquals(extensionPointDefinition.getName(), extensionPointDefinition2.getName());
	}
	
	
	
	@Test
	public void createExtensionTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.createExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test");
		List<ExtensionDefinition> list = extensionsCoreService.getExtensionsByExtensionPoint("test_extpoint1");
		assertEquals(1, list.size());
		ExtensionDefinition extensionDefinition = list.get(0);
		assertEquals("test_ext1", extensionDefinition.getModule());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void getExtensionTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");

		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("/test_ext1");
		assertEquals("test_ext1", extensionDefinition.getModule());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		
	}
	
	@Test
	public void getExtensionsTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		extensionsCoreService.removeExtensionPoint("/test_extpoint2");
		extensionsCoreService.createExtensionPoint("/test_extpoint2", "test_extpoint2", "Test");

		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.createExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test Ext 1");
		extensionsCoreService.removeExtension("/test_ext11");
		extensionsCoreService.createExtension("/test_ext11", "test_ext11", "test_extpoint1", "Test Ext 11");
		extensionsCoreService.removeExtension("/test_ext2");
		extensionsCoreService.createExtension("/test_ext2", "test_ext2", "test_extpoint2", "Test Ext 2");
		extensionsCoreService.removeExtension("/test_ext22");
		extensionsCoreService.createExtension("/test_ext22", "test_ext22", "test_extpoint2", "Test Ext 22");
		
		List<ExtensionDefinition> list = extensionsCoreService.getExtensionsByExtensionPoint("test_extpoint1");
		assertEquals(2, list.size());
		list = extensionsCoreService.getExtensionsByExtensionPoint("test_extpoint2");
		assertEquals(2, list.size());
		ExtensionDefinition extensionDefinition = list.get(0);
		assertEquals("test_extpoint2", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext 2", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.removeExtension("/test_ext11");
		extensionsCoreService.removeExtension("/test_ext2");
		extensionsCoreService.removeExtension("/test_ext22");
		
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.removeExtensionPoint("/test_extpoint2");
	}
	
	@Test
	public void updatetExtensionTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.createExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("/test_ext1");
		
		assertEquals("test_ext1", extensionDefinition.getModule());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext", extensionDefinition.getDescription());
		extensionsCoreService.updateExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test Ext 2");
		extensionDefinition = extensionsCoreService.getExtension("/test_ext1");
		assertEquals("test_ext1", extensionDefinition.getModule());
		assertEquals("Test Ext 2", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
	}
	
	@Test
	public void removeExtensionTest() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
		extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("/test_ext1");
		extensionsCoreService.createExtension("/test_ext1", "test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("/test_ext1");
		extensionsCoreService.removeExtension("/test_ext1");
		extensionDefinition = extensionsCoreService.getExtension("/test_ext1");
		assertNull(extensionDefinition);
		
		extensionsCoreService.removeExtensionPoint("/test_extpoint1");
	}
	
	@Test
	public void parseExtensionTest() throws ExtensionsException {
		ExtensionDefinition extensionDefinition = new ExtensionDefinition();
		extensionDefinition.setLocation("/test_ext1");
		extensionDefinition.setModule("test_ext1");
		extensionDefinition.setExtensionPoint("test_extpoint1");
		extensionDefinition.setDescription("Test");
		extensionDefinition.setCreatedAt(new Timestamp(new Date().getTime()));
		extensionDefinition.setCreatedBy("test_user");
		String json = extensionsCoreService.serializeExtension(extensionDefinition);
		System.out.println(json);
		ExtensionDefinition extensionDefinition2 = extensionsCoreService.parseExtension(json);
		assertEquals(extensionDefinition.getLocation(), extensionDefinition2.getLocation());
	}

}
