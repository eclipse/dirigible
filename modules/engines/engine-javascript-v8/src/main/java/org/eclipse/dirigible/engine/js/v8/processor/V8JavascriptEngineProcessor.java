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
package org.eclipse.dirigible.engine.js.v8.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;

/**
 * The V8 Javascript Engine Processor.
 */
public class V8JavascriptEngineProcessor implements IJavascriptEngineProcessor {

	/** The V8 engine executor. */
	@Inject
	private V8JavascriptEngineExecutor v8EngineExecutor;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor#executeService(java.lang.String)
	 */
	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		v8EngineExecutor.executeServiceModule(module, executionContext);
	}

}
