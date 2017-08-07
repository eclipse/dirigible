package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

public class GitPushModel extends BaseGitProjectModel {

	@ApiModelProperty(value = "The Commit Message", required = true, example = "Updates README.md")
	private String commitMessage;

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

}
