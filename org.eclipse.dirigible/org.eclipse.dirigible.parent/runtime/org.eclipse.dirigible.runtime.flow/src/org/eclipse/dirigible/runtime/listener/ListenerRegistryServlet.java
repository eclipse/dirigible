/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

import org.eclipse.dirigible.runtime.flow.FlowRegistryServlet;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServiceServlet;

public class ListenerRegistryServlet extends AbstractRegistryServiceServlet {

	private static final long serialVersionUID = -8255379751142002763L;

	@Override
	protected String getServletMapping() {
		return "/listener/";
	}

	@Override
	protected String getFileExtension() {
		return ".listener";
	}

	@Override
	protected String getRequestProcessingFailedMessage() {
		return "Listener execution failed.";
	}

	@Override
	protected String getServicesFolder() {
		return FlowRegistryServlet.INTEGRATION_FOLDER;
	}

}
