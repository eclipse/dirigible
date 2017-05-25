package org.eclipse.dirigible.commons.config;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ConfigurationFacadeTest {
	
	@Test
	public void initTest() {
		String value = ConfigurationFacade.getInstance().get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("local", value);
	}
	
	@Test
	public void updateTest() {
		String value = ConfigurationFacade.getInstance().get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("local", value);
		
		System.setProperty("DIRIGIBLE_INSTANCE_NAME", "test");
		
		ConfigurationFacade.getInstance().update();
		
		value = ConfigurationFacade.getInstance().get("DIRIGIBLE_INSTANCE_NAME");
		assertEquals("test", value);
		
		System.setProperty("DIRIGIBLE_INSTANCE_NAME", "local");
		
		ConfigurationFacade.getInstance().update();
	}
	
	@Test
	public void customTest() {
		ConfigurationFacade.getInstance().load("/test.properties");
		String value = ConfigurationFacade.getInstance().get("DIRIGIBLE_TEST_PROPERTY");
		assertEquals("test", value);
	}

}
