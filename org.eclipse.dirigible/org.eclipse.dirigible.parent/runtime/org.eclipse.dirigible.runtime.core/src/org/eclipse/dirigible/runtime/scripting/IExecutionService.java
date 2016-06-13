/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IExecutionService {

	/**
	 * Executes a given module by a registry relative path, execution context and engine type
	 *
	 * @param module
	 * @param executionContext
	 * @param serviceType
	 * @return
	 */
	public Object execute(HttpServletRequest request, HttpServletResponse response, String module, Map<Object, Object> executionContext,
			String serviceType);

	/**
	 * Utility function for creating an initial execution context
	 *
	 * @return
	 */
	public Map createContext();

}
