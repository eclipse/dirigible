/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.debug;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.bridge.DirigibleBridge;
import org.eclipse.dirigible.repository.ext.debug.DebugManager;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.JavaScriptServlet;

/**
 * Servlet for JavaScript scripts execution
 */
public class JavaScriptDebugServlet extends JavaScriptServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(JavaScriptDebugServlet.class);

	private static WebSocketDebugBridgeServletInternal webSocketDebugBridgeServletInternal;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			setupDebugChannel();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void setupDebugChannel() throws IOException {

		logger.debug("Setting debug channel internal ...");

		webSocketDebugBridgeServletInternal = new WebSocketDebugBridgeServletInternal();

		DirigibleBridge.BRIDGES.put("websocket_debug_channel_internal", webSocketDebugBridgeServletInternal);

		logger.debug("Debug channel internal has been set.");

	}

	@Override
	public JavaScriptDebuggingExecutor createExecutor(HttpServletRequest request) throws IOException {

		logger.debug("entering JavaScriptDebugServlet.createExecutor()");

		// DebugModel debugModel = (DebugModel) request.getSession(true).getAttribute(DebugModel.DEBUG_MODEL);
		DebugModel debugModel = DebugManager.getDebugModel(RequestUtils.getUser(request));
		if (debugModel == null) {
			String error = "Debug model is not present in the session";
			logger.error(error);
			throw new IOException(error);
		}

		String rootPath = getScriptingRegistryPath(request);
		logger.debug("rootPath=" + rootPath);
		JavaScriptDebuggingExecutor executor = new JavaScriptDebuggingExecutor(getRepository(request), rootPath, REGISTRY_SCRIPTING_DEPLOY_PATH,
				debugModel);

		logger.debug("exiting JavaScriptDebugServlet.createExecutor()");

		return executor;
	}

}
