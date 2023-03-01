/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package test.org.eclipse.dirigible.core.generation.velocity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.core.generation.velocity.VelocityGenerationEngine;
import org.junit.Test;

/**
 * The Class VelocityGeneratorTest.
 */
public class VelocityGeneratorTest {
	
	/**
	 * Generate.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void generate() throws IOException {
		IGenerationEngine generationEngine = new VelocityGenerationEngine();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("testParameter", "testValue");
		byte[] result = generationEngine.generate(parameters, "/location", "test $testParameter".getBytes(), null, null);
		assertEquals("test testValue", new String(result));
	}

}
