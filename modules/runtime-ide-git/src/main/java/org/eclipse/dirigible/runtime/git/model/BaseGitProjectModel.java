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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class BaseGitProjectModel extends BaseGitModel {

	@ApiModelProperty(value = "List of projects, on which a Git Operation will be performed", required = true, example = "[core_api]")
	private List<String> projects;

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
}
