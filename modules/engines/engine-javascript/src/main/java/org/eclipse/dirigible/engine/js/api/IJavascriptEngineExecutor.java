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

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;

/**
 * The Javascript Engine Executor interface.
 */
public interface IJavascriptEngineExecutor extends IScriptEngineExecutor {

	/** The Constant DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT. */
	public static final String DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT = "DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT";
	
	/** The Constant DIRIGIBLE_JAVASCRIPT_DEBUG_ENABLED. */
	public static final String DIRIGIBLE_JAVASCRIPT_DEBUG_ENABLED = "DIRIGIBLE_JAVASCRIPT_DEBUG_ENABLED";

	/** The Constant DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER. */
	public static final String DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER = "dirigible-js-engine";

	/** The Constant JAVASCRIPT_ENGINE_TYPE. */
	public static final String JAVASCRIPT_ENGINE_TYPE = "__engine";

	/** The Constant JAVASCRIPT_TYPE_GRAALIUM. */
	public static final String JAVASCRIPT_TYPE_GRAALIUM = "graalium";

	/** The Constant JAVASCRIPT_TYPE_DEFAULT. */
	public static final String JAVASCRIPT_TYPE_DEFAULT = "javascript";

	/** The Constant CONSOLE. */
	public static final String CONSOLE = "console";
	
	/** The Constant CONTEXT. */
	public static final String CONTEXT = "__context";

	/**
	 * Execute service module.
	 *
	 * @param module the module
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

}
