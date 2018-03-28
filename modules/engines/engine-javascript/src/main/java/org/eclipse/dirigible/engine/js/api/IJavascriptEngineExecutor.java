/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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

	/** The Constant DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER. */
	public static final String DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER = "dirigible-js-engine";

	/** The Constant JAVASCRIPT_ENGINE_TYPE. */
	public static final String JAVASCRIPT_ENGINE_TYPE = "engine";

	/** The Constant JAVASCRIPT_TYPE_RHINO. */
	public static final String JAVASCRIPT_TYPE_RHINO = "rhino";

	/** The Constant JAVASCRIPT_TYPE_NASHORN. */
	public static final String JAVASCRIPT_TYPE_NASHORN = "nashorn";

	/** The Constant JAVASCRIPT_TYPE_V8. */
	public static final String JAVASCRIPT_TYPE_V8 = "v8";

	/** The Constant JAVASCRIPT_TYPE_DEFAULT. */
	public static final String JAVASCRIPT_TYPE_DEFAULT = "javascript";

	/** The Constant CONSOLE. */
	public static final String CONSOLE = "console";
	
	/** The Constant CONTEXT. */
	public static final String CONTEXT = "context";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

}
