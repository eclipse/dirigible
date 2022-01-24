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
package org.eclipse.dirigible.engine.command.processor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

/**
 * The Command Engine Processor.
 */
public class CommandEngineProcessor {

	private CommandEngineExecutor engineExecutor = new CommandEngineExecutor();

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
