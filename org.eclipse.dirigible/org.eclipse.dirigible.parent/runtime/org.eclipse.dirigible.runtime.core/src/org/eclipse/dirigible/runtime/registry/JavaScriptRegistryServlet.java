/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

public class JavaScriptRegistryServlet extends AbstractRegistryServiceServlet {

	private static final long serialVersionUID = 1663850590192705089L;

	@Override
	protected String getServletMapping() {
		return "/js/";
	}

	@Override
	protected String getFileExtension() {
		return ".js";
	}

	@Override
	protected String getRequestProcessingFailedMessage() {
		return Messages.getString("JavascriptRegistryServlet.REQUEST_PROCESSING_FAILED_S");
	}
}
