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

public class BaseGitModel {

	@ApiModelProperty(value = "The User Name", required = true, example = "dirigible")
	private String username;

	@ApiModelProperty(value = "Base64 Encoded Password", required = true, example = "ZGlyaWdpYmxl")
	private String password;

	@ApiModelProperty(value = "The E-mail Address", example = "dirigible@gmail.com")
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
