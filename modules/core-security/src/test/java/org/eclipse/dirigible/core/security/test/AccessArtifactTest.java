package org.eclipse.dirigible.core.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessArtifactConstraint;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.junit.Test;

public class AccessArtifactTest {
	
	@Test
	public void serializeTest() {
		AccessArtifact access = new AccessArtifact();
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(0).setUri("/myproject/myfolder/myartifact1.txt");
		access.getConstraints().get(0).setMethod("*");
		access.getConstraints().get(0).getRoles().add("myrole1");
		access.getConstraints().get(0).getRoles().add("myrole2");
		access.getConstraints().add(new AccessArtifactConstraint());
		access.getConstraints().get(1).setUri("/myproject/myfolder/myartifact2.txt");
		access.getConstraints().get(1).setMethod("GET");
		access.getConstraints().get(1).getRoles().add("myrole3");
		access.getConstraints().get(1).getRoles().add("myrole4");
		assertNotNull(access.serialize());
	}

	@Test
	public void parseTest() throws IOException {
		String json = IOUtils.toString(AccessArtifactTest.class.getResourceAsStream("/access/test.access"), Configuration.UTF8);
		AccessArtifact access = AccessArtifact.parse(json);
		assertEquals("*", access.getConstraints().get(0).getMethod());
	}
	
	@Test
	public void combineTest() throws IOException {
		List<AccessDefinition> accessDefinitions = new ArrayList<AccessDefinition>();
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setUri("/myproject/myfolder/myartifact1.txt");
		accessDefinition.setMethod("*");
		accessDefinition.setRole("myrole1");
		accessDefinitions.add(accessDefinition);
		accessDefinition = new AccessDefinition();
		accessDefinition.setUri("/myproject/myfolder/myartifact1.txt");
		accessDefinition.setMethod("*");
		accessDefinition.setRole("myrole2");
		accessDefinitions.add(accessDefinition);
		accessDefinition = new AccessDefinition();
		accessDefinition.setUri("/myproject/myfolder/myartifact2.txt");
		accessDefinition.setMethod("GET");
		accessDefinition.setRole("myrole3");
		accessDefinitions.add(accessDefinition);
		accessDefinition = new AccessDefinition();
		accessDefinition.setUri("/myproject/myfolder/myartifact2.txt");
		accessDefinition.setMethod("GET");
		accessDefinition.setRole("myrole4");
		accessDefinitions.add(accessDefinition);
		AccessArtifact access = AccessArtifact.combine(accessDefinitions);
		assertEquals("*", access.getConstraints().get(0).getMethod());
	}
	
	@Test
	public void divideTest() throws IOException {
		String json = IOUtils.toString(AccessArtifactTest.class.getResourceAsStream("/access/test.access"), Configuration.UTF8);
		AccessArtifact access = AccessArtifact.parse(json);
		List<AccessDefinition> accessDefinitions = access.divide();
		assertEquals("*", accessDefinitions.get(1).getMethod());
	}
}
