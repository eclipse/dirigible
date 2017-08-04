package org.eclipse.dirigible.runtime.git.model;

import java.util.List;

public class BaseGitProjectModel extends BaseGitModel {

	private List<String> projects;

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
}
