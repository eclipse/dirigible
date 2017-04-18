/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.air.engines;

import javax.servlet.annotation.WebServlet;

import org.eclipse.dirigible.runtime.web.WebRegistryServlet;

/**
 * Wrapper for WebServlet
 */
@WebServlet({ "/services/web/*", "/services/web-secured/*", "/services/web-sandbox/*" })
public class WebContentServlet extends WebRegistryServlet {
	private static final long serialVersionUID = 1L;

}
