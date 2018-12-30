/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package test.org.eclipse.dirigible.runtime.ide.generation.mustache;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.runtime.ide.generation.processor.GenerationProcessor;
import org.junit.Test;

import com.github.mustachejava.util.DecoratedCollection;

public class MustacheGeneratorTest {
	
	@Test
	public void generate() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("testParameter", "testValue");
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{testParameter}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test testValue", new String(result));
	}
	
	@Test
	public void generateCollectionDecorated() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("elements", new DecoratedCollection(Arrays.asList(
					new HashMap<String, Object>() {{
						put("name", "name1");
					}},
					new HashMap<String, Object>() {{
						put("name", "name2");
					}},
					new HashMap<String, Object>() {{
						put("name", "name3");
					}}
				)));
		}};

		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements}}{{value.name}}{{^last}}, {{/last}}{{/elements}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1, name2, name3", new String(result));
	}
	
	@Test
	public void generateCollectionSimple() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("elements", Arrays.asList(
					new HashMap<String, Object>() {{
						put("name", "name1");
					}},
					new HashMap<String, Object>() {{
						put("name", "name2");
					}},
					new HashMap<String, Object>() {{
						put("name", "name3");
					}}
				));
		}};

		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements}}{{name}} {{/elements}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1 name2 name3 ", new String(result));
	}
	
	@Test
	public void generateCollectionDecoratedDefault() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("elements", Arrays.asList(
					new HashMap<String, Object>() {{
						put("name", "name1");
					}},
					new HashMap<String, Object>() {{
						put("name", "name2");
					}},
					new HashMap<String, Object>() {{
						put("name", "name3");
					}}
				));
		}};

		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements_}}{{value.name}}{{^last}}, {{/last}}{{/elements_}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1, name2, name3", new String(result));
	}

	@Test
	public void generateCollectionDecoratedNested() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("elements", Arrays.asList(
					new HashMap<String, Object>() {{
						put("properties", Arrays.asList(
								new HashMap<String, Object>() {{
									put("table", "table1");
								}},
								new HashMap<String, Object>() {{
									put("table", "table2");
								}}));
					}})
			);
		}};
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements_}}{{#value.properties_}}{{value.table}}{{^last}}, {{/last}}{{/value.properties_}}{{/elements_}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test table1, table2", new String(result));
	}
}
