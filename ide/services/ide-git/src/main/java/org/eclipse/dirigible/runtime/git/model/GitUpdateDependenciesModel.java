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
 * The Git Update Dependencies Model.
 */
public class GitUpdateDependenciesModel extends BaseGitProjectModel {

	@ApiModelProperty(value = "Whether to publish the project(s) after update of dependencies", example = "true")
	private boolean publish;

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
