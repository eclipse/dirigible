/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;

/**
 * The Rhino Javascript Engine Processor.
 */
public class RhinoJavascriptEngineProcessor implements IJavascriptEngineProcessor {

	@Inject
	private RhinoJavascriptEngineExecutor rhinoEngineExecutor;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor#executeService(java.lang.String)
	 */
	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		rhinoEngineExecutor.executeServiceModule(module, executionContext);
	}

}
