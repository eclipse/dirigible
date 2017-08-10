package org.eclipse.dirigible.core.workspace.json;

import java.util.ArrayList;
import java.util.List;

public class Workspace {

	private String name;

	private String path;

	private String type = "workspace";

	private List<Project> projects = new ArrayList<Project>();

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

	public List<Project> getProjects() {
		return projects;
	}

	public void set(List<Project> projects) {
		this.projects = projects;
	}

}
