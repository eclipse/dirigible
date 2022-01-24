/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.debugger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;

public class GraalVMJavascriptDebugProcessor {

	private static final Set<String> OPEN_USER_SESSIONS = ConcurrentHashMap.newKeySet();

	public static boolean haveUserSession(String userName) {
		if (Boolean.parseBoolean(Configuration.get(GraalVMJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED))) {
			return OPEN_USER_SESSIONS.contains(userName); 
		}
		return false;
	}

	public static void addUserSession(String userName) {
		Configuration.set(GraalVMJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED, "true");
		OPEN_USER_SESSIONS.add(userName);
	}

	public static void clear() {
		Configuration.set(GraalVMJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED, "false");
		OPEN_USER_SESSIONS.clear();
	}

}
