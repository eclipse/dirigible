/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.workspace.service;

import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.repository.api.ICollection;

/**
 * The Workspace's Project.
 */
public class Project extends Folder implements IProject {

	/**
	 * Instantiates a new project.
	 *
	 * @param projectCollection
	 *            the project collection
	 */
	public Project(ICollection projectCollection) {
		super(projectCollection);
	}

}
