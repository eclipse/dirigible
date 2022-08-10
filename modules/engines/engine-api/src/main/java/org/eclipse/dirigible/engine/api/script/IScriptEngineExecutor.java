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
package org.eclipse.dirigible.engine.api.script;

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.IEngineExecutor;
import org.eclipse.dirigible.repository.api.RepositoryException;

/**
 * The Script Engine Executor interface.
 */
public interface IScriptEngineExecutor extends IEngineExecutor {

	/**
	 * Retrieve module.
	 *
	 * @param root
	 *            the root
	 * @param module
	 *            the module
	 * @return the module
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public Module retrieveModule(String root, String module) throws RepositoryException;

	/**
	 * Retrieve module.
	 *
	 * @param root
	 *            the root
	 * @param module
	 *            the module
	 * @param extension
	 *            the extension
	 * @return the module
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public Module retrieveModule(String root, String module, String extension) throws RepositoryException;
	
	/**
	 * Exists module.
	 *
	 * @param root
	 *            the root
	 * @param module
	 *            the module
	 * @return the exists
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public boolean existsModule(String root, String module) throws RepositoryException;

	/**
	 * Exists module.
	 *
	 * @param root
	 *            the root
	 * @param module
	 *            the module
	 * @param extension
	 *            the extension
	 * @return the exists
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public boolean existsModule(String root, String module, String extension) throws RepositoryException;

	/**
	 * Execute service module.
	 *
	 * @param module
	 *            the module
	 * @param executionContext
	 *            the execution context
	 * @return the object
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Execute service code.
	 *
	 * @param code
	 *            the code
	 * @param executionContext
	 *            the execution context
	 * @return the object
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Eval code.
	 *
	 * @param code the code
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	public Object evalCode(String code, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Eval module.
	 *
	 * @param module the module
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	public Object evalModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Execute method from module.
	 *
	 * @param module the module
	 * @param memberClass the member class
	 * @param memberClassMethod the member class method
	 * @param executionContext the execution context
	 * @return the object
	 */
	public Object executeMethodFromModule(String module, String memberClass, String memberClassMethod, Map<Object, Object> executionContext);

}
