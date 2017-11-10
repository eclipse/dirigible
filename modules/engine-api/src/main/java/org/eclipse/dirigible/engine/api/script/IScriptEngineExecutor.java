/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api.script;

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.repository.api.RepositoryException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IScriptEngineExecutor.
 */
public interface IScriptEngineExecutor {

	/**
	 * Retrieve module.
	 *
	 * @param root the root
	 * @param module the module
	 * @return the module
	 * @throws RepositoryException the repository exception
	 */
	public Module retrieveModule(String root, String module) throws RepositoryException;

	/**
	 * Retrieve module.
	 *
	 * @param root the root
	 * @param module the module
	 * @param extension the extension
	 * @return the module
	 * @throws RepositoryException the repository exception
	 */
	public Module retrieveModule(String root, String module, String extension) throws RepositoryException;

	/**
	 * Execute service module.
	 *
	 * @param module the module
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Execute service code.
	 *
	 * @param code the code
	 * @param executionContext the execution context
	 * @return the object
	 * @throws ScriptingException the scripting exception
	 */
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();

}
