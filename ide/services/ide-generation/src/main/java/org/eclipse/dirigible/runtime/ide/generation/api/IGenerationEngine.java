package org.eclipse.dirigible.runtime.ide.generation.api;

import java.io.IOException;
import java.util.Map;

public interface IGenerationEngine {
	
	public static final String ACTION_COPY = "copy";
	
	public static final String ACTION_GENERATE = "generate";
	
	public static final String GENERATION_ENGINE_DEFAULT = "mustache";
	
	public String getName();
	
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input) throws IOException;
	
	public byte[] generate(Map<String, Object> parameters, String location, byte[] input, String sm, String em) throws IOException;

}
