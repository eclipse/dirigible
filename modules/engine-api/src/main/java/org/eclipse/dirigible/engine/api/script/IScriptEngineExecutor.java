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

public interface IScriptEngineExecutor {

	public Module retrieveModule(String root, String module) throws RepositoryException;

	public Module retrieveModule(String root, String module, String extension) throws RepositoryException;

	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException;

	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException;

	public String getType();

}
