package org.eclipse.dirigible.runtime.ide.generation.processor;

import java.util.List;

public class GenerationTemplateMetadata {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private List<GenerationTemplateMetadataSource> sources;
	
	private List<GenerationTemplateMetadataParameter> parameters;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<GenerationTemplateMetadataSource> getSources() {
		return sources;
	}

	public void setSources(List<GenerationTemplateMetadataSource> sources) {
		this.sources = sources;
	}

	public List<GenerationTemplateMetadataParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<GenerationTemplateMetadataParameter> parameters) {
		this.parameters = parameters;
	}

}
