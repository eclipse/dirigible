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

/**
 * Wrapper for JavaScriptServlet
 */
@WebServlet({ "/services/js/*", "/services/js-secured/*", "/services/js-sandbox/*", "/services/js-debug/*" })
public class JavaScriptServlet extends org.eclipse.dirigible.runtime.js.JavaScriptServlet {
	private static final long serialVersionUID = 1L;
}
