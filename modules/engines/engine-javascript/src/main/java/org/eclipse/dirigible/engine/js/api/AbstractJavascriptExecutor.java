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
package org.eclipse.dirigible.engine.js.api;

import org.eclipse.dirigible.api.v3.http.HttpResponseFacade;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;

/**
 * The Abstract Javascript Executor.
 */
public abstract class AbstractJavascriptExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

	/** The Constant MODULE_EXT_JS. */
	public static final String MODULE_EXT_JS = ".js/";

	/** The Constant MODULE_EXT_MJS. */
	public static final String MODULE_EXT_MJS = ".mjs/";

	/** The Constant MODULE_EXT_RHINO. */
	public static final String MODULE_EXT_RHINO = ".rhino/";

	/** The Constant MODULE_EXT_NASHORN. */
	public static final String MODULE_EXT_NASHORN = ".nashorn/";

	/** The Constant MODULE_EXT_V8. */
	public static final String MODULE_EXT_V8 = ".v8/";

	/** The Constant MODULE_EXT_GRAALVM. */
	public static final String MODULE_EXT_GRAALVM = ".graalvm/";

	/**
	 * Force flush.
	 */
	protected void forceFlush() {
		try {
			if (HttpResponseFacade.isValid()) {
				HttpResponseFacade.flush();
				HttpResponseFacade.close();
			}
		} catch (Exception e) {
			// no need to log it
		}
	}

	

}
