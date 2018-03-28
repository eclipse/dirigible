package test.org.eclipse.dirigible.runtime.ide.generation.mustache;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
		Map<String, Object> parameters = new HashMap<>();
		List<Object> elements = new ArrayList<>();
		Map<String, Object> element = new HashMap<>();
		element.put("name", "name1");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name2");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name3");
		elements.add(element);
		Collection<List<Object>> decoratedElements = new DecoratedCollection(elements);
		parameters.put("elements", decoratedElements);
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements}}{{value.name}}{{^last}}, {{/last}}{{/elements}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1, name2, name3", new String(result));
	}
	
	@Test
	public void generateCollectionSimple() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<>();
		List<Object> elements = new ArrayList<>();
		Map<String, Object> element = new HashMap<>();
		element.put("name", "name1");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name2");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name3");
		elements.add(element);
		parameters.put("elements", elements);
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements}}{{name}} {{/elements}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1 name2 name3 ", new String(result));
	}
	
	@Test
	public void generateCollectionDecoratedDefault() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<>();
		List<Object> elements = new ArrayList<>();
		Map<String, Object> element = new HashMap<>();
		element.put("name", "name1");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name2");
		elements.add(element);
		element = new HashMap<>();
		element.put("name", "name3");
		elements.add(element);
		parameters.put("elements", elements);
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test {{#elements_}}{{value.name}}{{^last}}, {{/last}}{{/elements_}}".getBytes(), "{{", "}}", "mustache");
		assertEquals("test name1, name2, name3", new String(result));
	}

}
