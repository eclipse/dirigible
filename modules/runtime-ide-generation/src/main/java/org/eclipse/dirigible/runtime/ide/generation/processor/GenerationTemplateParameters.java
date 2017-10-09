package org.eclipse.dirigible.runtime.ide.generation.processor;

import java.util.Map;

public class GenerationTemplateParameters {
	
	private String template;
	
	private Map<String, Object> parameters;

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	

}
