package org.eclipse.dirigible.runtime.git.model;

public class GitUpdateDepenciesModel extends BaseGitProjectModel {

	private String repository;
	private String branch;
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
