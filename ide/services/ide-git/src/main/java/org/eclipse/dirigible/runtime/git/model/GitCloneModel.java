/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Git Clone Model.
 */
public class GitCloneModel extends BaseGitModel {

	@ApiModelProperty(value = "The Git Repository URL", example = "https://github.com/dirigiblelabs/sample_git_test.git", required = true)
	private String repository;

	@ApiModelProperty(value = "The Git Branch", example = "master", required = true)
	private String branch;

	@ApiModelProperty(value = "Whether to publish the project(s) after clone", example = "true")
	private boolean publish;

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
	 * Checks if is publish.
	 *
	 * @return true, if is publish
	 */
	public boolean isPublish() {
		return publish;
	}

	/**
	 * Sets the publish.
	 *
	 * @param publish the new publish
	 */
	public void setPublish(boolean publish) {
		this.publish = publish;
	}

}
