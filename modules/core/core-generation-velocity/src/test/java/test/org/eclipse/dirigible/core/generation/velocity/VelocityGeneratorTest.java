/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package test.org.eclipse.dirigible.core.generation.velocity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.core.generation.velocity.VelocityGenerationEngine;
import org.junit.Test;

public class VelocityGeneratorTest {
	
	@Test
	public void generate() throws IOException {
		IGenerationEngine generationEngine = new VelocityGenerationEngine();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("testParameter", "testValue");
		byte[] result = generationEngine.generate(parameters, "/location", "test $testParameter".getBytes(), null, null);
		assertEquals("test testValue", new String(result));
	}

}
