/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.command.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

/**
 * The Command Engine Processor.
 */
public class CommandEngineProcessor {

	@Inject
	private CommandEngineExecutor engineExecutor;

	/**
	 * Execute the command
	 * 
	 * @param module
	 * @throws ScriptingException
	 */
	public Object executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		return engineExecutor.executeServiceModule(module, executionContext);
	}

}
