/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow.log;

import org.eclipse.dirigible.runtime.registry.IRuntimeServiceDescriptor;

/**
 * Descriptor for the Flow Log Service
 */
public class FlowLogRuntimeServiceDescriptor implements IRuntimeServiceDescriptor {

	private final String name = "Flow Log";
	private final String description = "Flow Log Service gives the full log of the processed flows, their steps and context parameters.";
	private final String endpoint = "/flow-log";
	private final String documentation = "http://www.dirigible.io/help/service_flow_log.html";

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
