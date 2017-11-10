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
