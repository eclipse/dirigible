/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

public class EngineUtils {

	public static Set<String> getTypes() {
		return EngineUtilsFactory.getTypes();
	}

	public static Set<String> getAliases() {
		return EngineUtilsFactory.getAliases();
	}

	public static IScriptExecutor createExecutor(String type, HttpServletRequest request) throws IOException {
		return EngineUtilsFactory.createExecutor(type, request);
	}

	public static IScriptExecutor createExecutorByAlias(String alias, HttpServletRequest request) throws IOException {
		return EngineUtilsFactory.createExecutorByAlias(alias, request);
	}

}
