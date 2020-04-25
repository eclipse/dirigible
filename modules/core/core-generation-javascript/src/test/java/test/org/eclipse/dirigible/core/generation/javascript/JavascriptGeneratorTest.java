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
package test.org.eclipse.dirigible.core.generation.javascript;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.core.generation.javascript.JavascriptGenerationEngine;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.Before;
import org.junit.Test;

public class JavascriptGeneratorTest extends AbstractGuiceTest {
	
	private IRepository repository;

	@Before
	public void setUp() throws Exception {
		this.repository = getInjector().getInstance(IRepository.class);
	}
	
	@Test
	public void generate() throws IOException {
		IGenerationEngine generationEngine = new JavascriptGenerationEngine();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("handler", "my-handler.js");
		parameters.put("testParameter", "testValue");
		byte[] result = generationEngine.generate(parameters, "/location", "test $testParameter".getBytes(), null, null);
		assertEquals("test testValue", new String(result));
	}

}
