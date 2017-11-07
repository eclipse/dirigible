package org.eclipse.dirigible.runtime.ide.generation.processor;

public class GenerationTemplateMetadataSource {
	
	private String location;
	
	private String action;
	
	private String rename;
	
	private String start;
	
	private String end;

	public String getLocation() {
		return location;
	}

	public void setLocation(String name) {
		this.location = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
}
