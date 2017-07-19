package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

public class RolesArtifactTest extends AbstractGuiceTest {
	
	@Inject
	private ISecurityCoreService securityCoreService;
	
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
	}
	
	@Test
	public void serializeTest() {
		RoleDefinition[] roles = new RoleDefinition[2];
		roles[0] = new RoleDefinition();
		roles[0].setName("myrole1");
		roles[0].setDescription("Role1 Description");
		roles[1] = new RoleDefinition();
		roles[1].setName("myrole2");
		roles[1].setDescription("Role2 Description");
		assertNotNull(securityCoreService.serializeRoles(roles));
	}

	@Test
	public void parseTest() throws IOException {
		String json = IOUtils.toString(RolesArtifactTest.class.getResourceAsStream("/access/test.roles"), Configuration.UTF8);
		RoleDefinition[] roles = securityCoreService.parseRoles(json);
		assertEquals("myrole2", roles[1].getName());
	}
	
	
}
