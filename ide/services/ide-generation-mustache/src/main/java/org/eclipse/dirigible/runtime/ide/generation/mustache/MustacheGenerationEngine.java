package org.eclipse.dirigible.runtime.ide.generation.mustache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.dirigible.runtime.ide.generation.api.IGenerationEngine;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class MustacheGenerationEngine implements IGenerationEngine {
	
	public static final String ENGINE_NAME = "mustache";
	
	public static final String MUSTACHE_DEFAULT_START_SYMBOL = "{{";
	
	public static final String MUSTACHE_DEFAULT_END_SYMBOL = "}}";
	
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input) throws IOException {
		return generate(parameters, location, input, MUSTACHE_DEFAULT_START_SYMBOL, MUSTACHE_DEFAULT_END_SYMBOL);
	}

	@Override
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input, String sm, String em)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
		DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
		Mustache mustache = defaultMustacheFactory.compile(new InputStreamReader(new ByteArrayInputStream(input), StandardCharsets.UTF_8), location, sm, em);
		mustache.execute(writer, parameters);
		writer.flush();
		return baos.toByteArray();
	}

}
