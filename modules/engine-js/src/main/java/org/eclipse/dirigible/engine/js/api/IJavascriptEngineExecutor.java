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

public interface IJavascriptEngineExecutor extends IScriptEngineExecutor {

	public static final String DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT = "DIRIGIBLE_JS_ENGINE_TYPE_DEFAULT";
	public static final String DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER = "dirigible-js-engine";
	public static final String JAVASCRIPT_ENGINE_TYPE = "engine";
	public static final String JAVASCRIPT_TYPE_RHINO = "rhino";
	public static final String JAVASCRIPT_TYPE_NASHORN = "nashorn";
	public static final String JAVASCRIPT_TYPE_V8 = "v8";
	public static final String JAVASCRIPT_TYPE_DEFAULT = "javascript";

	public static final String CONSOLE = "console";

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

}
