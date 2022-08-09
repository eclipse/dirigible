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
package org.eclipse.dirigible.engine.js.processor;

import static java.text.MessageFormat.format;

import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.api.EngineExecutorFactory;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Default Javascript Engine Executor.
 */
public class DefaultJavascriptEngineExecutor extends AbstractJavascriptExecutor implements IJavascriptEngineExecutor {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DefaultJavascriptEngineExecutor.class);

	/** The Constant JAVASCRIPT_ENGINE_EXECUTORS. */
	private static final ServiceLoader<IJavascriptEngineExecutor> JAVASCRIPT_ENGINE_EXECUTORS = ServiceLoader.load(IJavascriptEngineExecutor.class);
	
	/** The Constant ENGINE_NAME. */
	public static final String ENGINE_NAME = "Default JavaScript Engine";

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_DEFAULT;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

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
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		IJavascriptEngineExecutor engine = getJavascriptEngine();
		return engine.executeServiceModule(module, executionContext);
	}

	/**
	 * Execute service code.
	 *
	 * @param code the code
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		IJavascriptEngineExecutor engine = getJavascriptEngine();
		return engine.executeServiceCode(code, executionContext);
	}

	/**
	 * Eval code.
	 *
	 * @param code the code
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	@Override
	public Object evalCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		IJavascriptEngineExecutor engine = getJavascriptEngine();
		return engine.evalCode(code, executionContext);
	}

	/**
	 * Eval module.
	 *
	 * @param module the module
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	@Override
	public Object evalModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		IJavascriptEngineExecutor engine = getJavascriptEngine();
		return engine.evalModule(module, executionContext);
	}

	/**
	 * Gets the javascript engine.
	 *
	 * @return the javascript engine
	 * @throws ScriptingException the scripting exception
	 */
	private IJavascriptEngineExecutor getJavascriptEngine() throws ScriptingException {

		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT, IJavascriptEngineExecutor.JAVASCRIPT_TYPE_GRAALVM);

		for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(javascriptEngineType)) {
				try {
					return next.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		// backup
		try {
			try {
				return (IJavascriptEngineExecutor) Class.forName("org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor").newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			}
		} catch (ClassNotFoundException e) {
			throw new ScriptingException("No Javascript Engine registered. The default GraalJS is also not available.");
		}
		logger.error(format("Default Javascript Engine Executor not found."));
		return null;
	}

	/**
	 * Execute method from module.
	 *
	 * @param module the module
	 * @param memberClass the member class
	 * @param memberClassMethod the member class method
	 * @param executionContext the execution context
	 * @return the object
	 */
	@Override
	public Object executeMethodFromModule(String module, String memberClass, String memberClassMethod, Map<Object, Object> executionContext) {
		IJavascriptEngineExecutor engine = getJavascriptEngine();
		return engine.executeMethodFromModule(module, memberClass, memberClassMethod, executionContext);
	}
}
