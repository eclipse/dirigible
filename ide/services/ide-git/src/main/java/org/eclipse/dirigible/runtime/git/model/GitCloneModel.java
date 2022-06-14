/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.git.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Git Clone Model.
 */
public class GitCloneModel extends BaseGitModel {

	@ApiModelProperty(value = "The Git Repository URL", example = "https://github.com/dirigiblelabs/sample_git_test.git", required = true)
	private String repository;

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
