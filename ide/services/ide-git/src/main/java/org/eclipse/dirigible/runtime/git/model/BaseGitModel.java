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

import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Base Git Model.
 */
public class BaseGitModel {

	@ApiModelProperty(value = "The User Name", required = true, example = "dirigible")
	private String username;

	@ApiModelProperty(value = "Base64 Encoded Password", required = true, example = "ZGlyaWdpYmxl")
	private String password;

	@ApiModelProperty(value = "The E-mail Address", example = "dirigible@gmail.com")
	private String email;
	
	@ApiModelProperty(value = "The selected branch", required = false, example = "master")
	private String branch = ProjectMetadataManager.BRANCH_MASTER;

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the branch
	 * 
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * Sets the branch
	 * 
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	
}
