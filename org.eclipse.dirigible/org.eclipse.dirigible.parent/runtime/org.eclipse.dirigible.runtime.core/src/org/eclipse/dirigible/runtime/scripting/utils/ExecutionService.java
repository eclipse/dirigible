/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IExecutionService;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

/**
 * Execution Service provide access to the available engines on the current instance. It is exposed to the developers as
 * an injected object
 */
public class ExecutionService implements IExecutionService {

	private static final Logger logger = Logger.getLogger(ExecutionService.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.scripting.utils.IExecutionService#execute(java.lang.String, java.util.Map,
	 * java.lang.String)
	 */
	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response, String module, Map<Object, Object> executionContext,
			String serviceType) {
		try {
			Set<String> types = EngineUtils.getAliases();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(serviceType)) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutorByAlias(type, request);
					return scriptExecutor.executeServiceModule(request, response, module, executionContext);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.scripting.utils.IExecutionService#createContext()
	 */
	@Override
	public Map createContext() {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		return executionContext;
	}

}
