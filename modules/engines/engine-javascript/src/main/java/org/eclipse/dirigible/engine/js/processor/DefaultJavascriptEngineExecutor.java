/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.processor;

import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;

/**
 * The Default Javascript Engine Executor.
 */
public class DefaultJavascriptEngineExecutor extends AbstractJavascriptExecutor implements IJavascriptEngineExecutor {

	private static final ServiceLoader<IJavascriptEngineExecutor> JAVASCRIPT_ENGINE_EXECUTORS = ServiceLoader.load(IJavascriptEngineExecutor.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_DEFAULT;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT,
				IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
		for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(javascriptEngineType)) {
				return StaticInjector.getInjector().getInstance(next.getClass()).executeServiceModule(module, executionContext);
			}
		}
		throw new ScriptingException("No Javascript Engine registered");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT,
				IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
		for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(javascriptEngineType)) {
				return StaticInjector.getInjector().getInstance(next.getClass()).executeServiceCode(code, executionContext);
			}
		}
		throw new ScriptingException("No Javascript Engine registered");
	}

}
