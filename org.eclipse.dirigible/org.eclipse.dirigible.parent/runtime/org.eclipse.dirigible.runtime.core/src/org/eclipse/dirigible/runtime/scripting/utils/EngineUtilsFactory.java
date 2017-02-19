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

import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;

public class EngineUtilsFactory {

	public static Set<String> getTypes() {
		if (OSGiUtils.isOSGiEnvironment()) {
			return EngineUtilsFactoryOSGi.getTypes();
		}
		return EngineUtilsFactoryNonOSGi.getTypes();
	}

	public static Set<String> getAliases() {
		if (OSGiUtils.isOSGiEnvironment()) {
			return EngineUtilsFactoryOSGi.getAliases();
		}
		return EngineUtilsFactoryNonOSGi.getAliases();
	}

	public static IScriptExecutor createExecutor(String type, HttpServletRequest request) throws IOException {
		if (OSGiUtils.isOSGiEnvironment()) {
			return EngineUtilsFactoryOSGi.createExecutor(type, request);
		}
		return EngineUtilsFactoryNonOSGi.createExecutor(type, request);
	}

	public static IScriptExecutor createExecutorByAlias(String alias, HttpServletRequest request) throws IOException {
		if (OSGiUtils.isOSGiEnvironment()) {
			return EngineUtilsFactoryOSGi.createExecutorByAlias(alias, request);
		}
		return EngineUtilsFactoryNonOSGi.createExecutorByAlias(alias, request);
	}

}
