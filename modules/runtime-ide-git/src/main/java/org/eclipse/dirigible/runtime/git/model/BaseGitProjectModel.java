package org.eclipse.dirigible.runtime.git.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class BaseGitProjectModel extends BaseGitModel {

	@ApiModelProperty(value = "List of projects, on which a Git Operation will be performed", required = true, example = "[core_api]")
	private List<String> projects;

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
}
