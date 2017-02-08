/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

/**
 * Descriptor for the Workspace Service
 */
public class WorkspaceRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Workspace"; //$NON-NLS-1$
	private static final String description = "Workspace Service gives full access for management of projects artifacts within the User's workspace.";
	private static final String endpoint = "/workspace"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_workspace.html"; //$NON-NLS-1$

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

}
