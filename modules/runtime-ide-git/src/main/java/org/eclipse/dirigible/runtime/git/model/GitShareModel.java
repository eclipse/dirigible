/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class GitShareModel.
 */
public class GitShareModel extends BaseGitModel {

	/** The project. */
	@ApiModelProperty(hidden = true)
	private String project;

	/** The repository. */
	@ApiModelProperty(value = "The Git Repository URL", example = "https://github.com/dirigiblelabs/sample_git_test.git", required = true)
	private String repository;

	/** The branch. */
	@ApiModelProperty(value = "The Git Branch", example = "master", required = true)
	private String branch;

	/** The commit message. */
	@ApiModelProperty(value = "The Commit Message", example = "Initial Commit", required = true)
	private String commitMessage;

	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 *
	 * @param project the new project
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public String getRepository() {
		return repository;
	}

	/**
	 * Sets the repository.
	 *
	 * @param repository the new repository
	 */
	public void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Gets the branch.
	 *
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * Sets the branch.
	 *
	 * @param branch the new branch
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}

	/**
	 * Gets the commit message.
	 *
	 * @return the commit message
	 */
	public String getCommitMessage() {
		return commitMessage;
	}

	/**
	 * Sets the commit message.
	 *
	 * @param commitMessage the new commit message
	 */
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

}
