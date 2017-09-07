package org.eclipse.dirigible.core.workspace.json;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceDescriptor {

	private String name;

	private String path;

	private String type = "workspace";

	private List<ProjectDescriptor> projects = new ArrayList<ProjectDescriptor>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ProjectDescriptor> getProjects() {
		return projects;
	}

	public void set(List<ProjectDescriptor> projects) {
		this.projects = projects;
	}

}
