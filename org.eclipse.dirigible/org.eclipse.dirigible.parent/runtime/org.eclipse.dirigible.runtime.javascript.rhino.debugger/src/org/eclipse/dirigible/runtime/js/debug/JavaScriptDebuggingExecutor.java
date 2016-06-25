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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.JavaScriptExecutor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;

public class JavaScriptDebuggingExecutor extends JavaScriptExecutor {

	private static final Logger logger = Logger.getLogger(JavaScriptDebuggingExecutor.class);

	private static final String JAVA_SCRIPT_DEBUGGER = "JavaScript Debugger";

	private JavaScriptDebuggingExecutor(IRepository repository, String rootPath, String secondaryRootPath) {
		super(repository, rootPath, secondaryRootPath);
	}

	private DebugModel debugModel;

	public JavaScriptDebuggingExecutor(IRepository repository, String rootPath, String secondaryRootPath, DebugModel debugModel) {
		super(repository, rootPath, secondaryRootPath);
		this.debugModel = debugModel;
	}

	@Override
	public void beforeExecution(HttpServletRequest request, HttpServletResponse response, String module, Object context) {
		logger.debug("entering JavaScriptDebuggingExecutor.beforeExecution()");

		if (context instanceof Context) {
			Context rhinoContext = (Context) context;
			ErrorReporter reporter = new InvocationErrorReporter();
			rhinoContext.setErrorReporter(reporter);

			logger.debug("creating JavaScriptDebugger");
			JavaScriptDebugger debugger = new JavaScriptDebugger(debugModel, request);
			rhinoContext.setDebugger(debugger, JAVA_SCRIPT_DEBUGGER);
			logger.debug("created JavaScriptDebugger");

			String userId = RequestUtils.getUser(request);
			WebSocketDebugSessionServletInternal.sendCurrentDebugModelSessionsToUser(userId, debugModel);

			rhinoContext.setGeneratingDebug(true);
			rhinoContext.setOptimizationLevel(-1);
		}
		logger.debug("exiting JavaScriptDebuggingExecutor.beforeExecution()");
	}

}
