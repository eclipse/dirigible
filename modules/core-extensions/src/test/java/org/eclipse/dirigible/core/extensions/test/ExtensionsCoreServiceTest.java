package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.extensions.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.ExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.ExtensionsException;
import org.eclipse.dirigible.core.extensions.IExtensionsCoreService;
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
	public void createExtensionPoint() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		List<ExtensionPointDefinition> list = extensionsCoreService.getExtensionPoints();
		assertEquals(1, list.size());
		ExtensionPointDefinition extensionPointDefinition = list.get(0);
		System.out.println(extensionPointDefinition.toString());
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void getExtensionPoint() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPoint("test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void updatetExtensionPoint() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPoint("test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.updateExtensionPoint("test_extpoint1", "Test 2");
		extensionPointDefinition = extensionsCoreService.getExtensionPoint("test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test 2", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void removeExtensionPoint() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.getExtensionPoint("test_extpoint1");
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test", extensionPointDefinition.getDescription());
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionPointDefinition = extensionsCoreService.getExtensionPoint("test_extpoint1");
		assertNull(extensionPointDefinition);
	}
	
	
	
	
	
	
	
	
	@Test
	public void createExtension() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("test_ext1", "test_extpoint1", "Test");
		List<ExtensionDefinition> list = extensionsCoreService.getExtensions();
		assertEquals(1, list.size());
		ExtensionDefinition extensionDefinition = list.get(0);
		assertEquals("test_ext1", extensionDefinition.getLocation());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void getExtension() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");

		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("test_ext1");
		assertEquals("test_ext1", extensionDefinition.getLocation());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		
	}
	
	@Test
	public void getExtensions() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		extensionsCoreService.removeExtensionPoint("test_extpoint2");
		extensionsCoreService.createExtensionPoint("test_extpoint2", "Test");

		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("test_ext1", "test_extpoint1", "Test Ext 1");
		extensionsCoreService.removeExtension("test_ext11");
		extensionsCoreService.createExtension("test_ext11", "test_extpoint1", "Test Ext 11");
		extensionsCoreService.removeExtension("test_ext2");
		extensionsCoreService.createExtension("test_ext2", "test_extpoint2", "Test Ext 2");
		extensionsCoreService.removeExtension("test_ext22");
		extensionsCoreService.createExtension("test_ext22", "test_extpoint2", "Test Ext 22");
		
		List<ExtensionDefinition> list = extensionsCoreService.getExtensions();
		assertEquals(4, list.size());
		ExtensionDefinition extensionDefinition = list.get(0);
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext 1", extensionDefinition.getDescription());
		
		list = extensionsCoreService.getExtensionsByExtensionPoint("test_extpoint1");
		assertEquals(2, list.size());
		list = extensionsCoreService.getExtensionsByExtensionPoint("test_extpoint2");
		assertEquals(2, list.size());
		extensionDefinition = list.get(0);
		assertEquals("test_extpoint2", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext 2", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtension("test_ext11");
		extensionsCoreService.removeExtension("test_ext2");
		extensionsCoreService.removeExtension("test_ext22");
		
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.removeExtensionPoint("test_extpoint2");
	}
	
	@Test
	public void updatetExtension() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("test_ext1");
		
		assertEquals("test_ext1", extensionDefinition.getLocation());
		assertEquals("test_extpoint1", extensionDefinition.getExtensionPoint());
		assertEquals("Test Ext", extensionDefinition.getDescription());
		extensionsCoreService.updateExtension("test_ext1", "test_extpoint1", "Test Ext 2");
		extensionDefinition = extensionsCoreService.getExtension("test_ext1");
		assertEquals("test_ext1", extensionDefinition.getLocation());
		assertEquals("Test Ext 2", extensionDefinition.getDescription());
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}
	
	@Test
	public void removeExtension() throws ExtensionsException {
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		
		extensionsCoreService.removeExtension("test_ext1");
		extensionsCoreService.createExtension("test_ext1", "test_extpoint1", "Test Ext");
		ExtensionDefinition extensionDefinition = extensionsCoreService.getExtension("test_ext1");
		extensionsCoreService.removeExtension("test_ext1");
		extensionDefinition = extensionsCoreService.getExtension("test_ext1");
		assertNull(extensionDefinition);
		
		extensionsCoreService.removeExtensionPoint("test_extpoint1");
	}

}
