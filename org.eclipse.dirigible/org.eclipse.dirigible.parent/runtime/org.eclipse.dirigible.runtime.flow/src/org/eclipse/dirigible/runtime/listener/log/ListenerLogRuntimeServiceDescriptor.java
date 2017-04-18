/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener.log;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Job Log Service
 */
public class ListenerLogRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Job Log"; //$NON-NLS-1$
	private final String description = "Job Log Service gives the full log of the processed Jobs";
	private final String endpoint = "/job-log"; //$NON-NLS-1$
	private final String documentation = "http://www.dirigible.io/help/service_job_log.html"; //$NON-NLS-1$

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
