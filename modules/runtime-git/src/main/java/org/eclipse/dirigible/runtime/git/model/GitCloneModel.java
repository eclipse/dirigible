package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

public class GitCloneModel extends BaseGitModel {

	@ApiModelProperty(value = "The Git Repository URL", example = "https://github.com/dirigiblelabs/sample_git_test.git", required = true)
	private String repository;

	@ApiModelProperty(value = "The Git Branch", example = "master", required = true)
	private String branch;

	@ApiModelProperty(value = "Whether to publish the project(s) after clone", example = "true")
	private boolean publish;

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

}
