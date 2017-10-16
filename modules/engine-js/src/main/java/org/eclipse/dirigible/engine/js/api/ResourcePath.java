package org.eclipse.dirigible.engine.js.api;

public class ResourcePath {

	private String module;

	private String path;

	public ResourcePath() {
	}

	public ResourcePath(String module, String path) {
		super();
		this.module = module;
		this.path = path;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
