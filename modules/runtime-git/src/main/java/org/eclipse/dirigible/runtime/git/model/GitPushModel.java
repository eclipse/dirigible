package org.eclipse.dirigible.runtime.git.model;

public class GitPushModel extends BaseGitProjectModel {

	private String commitMessage;

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

}
