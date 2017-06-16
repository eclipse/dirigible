package org.eclipse.dirigible.core.extensions.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.extensions.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.ExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.ExtensionsException;
import org.junit.Before;
import org.junit.Test;

public class ExtensionsCoreServiceTest extends AbstractGuiceTest {
	
	@Inject
	private ExtensionsCoreService extensionsCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.extensionsCoreService = getInjector().getInstance(ExtensionsCoreService.class);
	}
	
	@Test
	public void createExtensionPoint() throws ExtensionsException {
		extensionsCoreService.createExtensionPoint("test_extpoint1", "Test");
		List<ExtensionPointDefinition> list = extensionsCoreService.getExtensionPoints();
		assertEquals(1, list.size());
		ExtensionPointDefinition extensionPointDefinition = list.get(0);
		assertEquals("test_extpoint1", extensionPointDefinition.getLocation());
		assertEquals("Test", extensionPointDefinition.getDescription());
	}

}
