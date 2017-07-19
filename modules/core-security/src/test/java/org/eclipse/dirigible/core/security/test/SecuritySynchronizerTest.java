package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessArtifactConstraint;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.security.synchronizer.SecuritySynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

public class SecuritySynchronizerTest extends AbstractGuiceTest {
	
	@Inject
	private ISecurityCoreService securityCoreService;
	
	@Inject
	private SecuritySynchronizer securityPublisher;
	
	@Inject
	private IRepository repository;
	
	@Before
	public void setUp() throws Exception {
		this.securityCoreService = getInjector().getInstance(SecurityCoreService.class);
		this.securityPublisher = getInjector().getInstance(SecuritySynchronizer.class);
		this.repository = getInjector().getInstance(IRepository.class);
	}
	
	@Test
	public void createAccessTest() throws SecurityException, IOException, AccessException {
		securityPublisher.registerPredeliveredAccess("/access/test.access");
		
		AccessArtifact access = new AccessArtifact();
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(0).setUri("/myproject/myfolder/myartifact3.txt");
		access.getConstraints().get(0).setMethod("*");
		access.getConstraints().get(0).getRoles().add("myrole1");
		access.getConstraints().get(0).getRoles().add("myrole2");
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(1).setUri("/myproject/myfolder/myartifact4.txt");
		access.getConstraints().get(1).setMethod("GET");
		access.getConstraints().get(1).getRoles().add("myrole3");
		access.getConstraints().get(1).getRoles().add("myrole4");
		String json = access.serialize();
		
		repository.createResource(IRepositoryStructure.REGISTRY_PUBLIC + "/access/test.access", json.getBytes());
		
		securityPublisher.synchronize();
		
		AccessDefinition accessDefinition = securityCoreService.getAccessDefinition("/myproject/myfolder/myartifact3.txt", "*", "myrole1");
		assertNotNull(accessDefinition);
		assertTrue(securityCoreService.isAccessAllowed("/myproject/myfolder/myartifact3.txt", "GET", "myrole1"));
	}
	
	@Test
	public void cleanupAccessTest() throws SecurityException, IOException, AccessException {
		createAccessTest();
		
		repository.removeResource(IRepositoryStructure.REGISTRY_PUBLIC + "/access/test.access");
		
		securityPublisher.synchronize();
		
		AccessDefinition accessDefinition = securityCoreService.getAccessDefinition("/myproject/myfolder/myartifact3.txt", "GET", "myrole1");
		assertNull(accessDefinition);
		assertFalse(securityCoreService.isAccessAllowed("/myproject/myfolder/myartifact3.txt", "GET", "myrole1"));
	}
	

}
