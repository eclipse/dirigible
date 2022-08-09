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
package org.eclipse.dirigible.engine.js.graalvm.processor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;

/**
 * The GraalVM Javascript Engine Processor.
 */
public class GraalVMJavascriptEngineProcessor implements IJavascriptEngineProcessor {

	/** The GraalVM engine executor. */
	private GraalVMJavascriptEngineExecutor graalVMEngineExecutor = new GraalVMJavascriptEngineExecutor();

	/**
	 * Execute service.
	 *
	 * @param module the module
	 * @throws ScriptingException the scripting exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor#executeService(java.lang.String)
	 */
	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		graalVMEngineExecutor.executeServiceModule(module, executionContext);
	}

}
