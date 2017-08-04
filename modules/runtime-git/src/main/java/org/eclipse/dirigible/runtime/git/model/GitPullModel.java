package org.eclipse.dirigible.runtime.git.model;

public class GitPullModel extends BaseGitProjectModel {

	private boolean publish;

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}
}
