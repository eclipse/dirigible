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
 * Descriptor for the Workspace Publish Service
 */
public class WorkspacePublishRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private static final String name = "Publish"; //$NON-NLS-1$
	private static final String description = "Publish Service performs publication of a single artifact from the User's workspace to the Registry";
	private static final String endpoint = "/publish"; //$NON-NLS-1$
	private static final String documentation = "http://www.dirigible.io/help/service_publish.html"; //$NON-NLS-1$

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
