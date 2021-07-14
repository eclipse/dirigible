/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.command.processor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.api.v3.http.HttpResponseFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.process.Piper;
import org.eclipse.dirigible.commons.process.ProcessUtils;
import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.command.definition.CommandDefinition;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Command Engine Executor.
 */
public class CommandEngineExecutor extends AbstractScriptExecutor implements IScriptEngineExecutor {
	
	private static final String REPOSITORY_ROOT_FOLDER = "REPOSITORY_ROOT_FOLDER";

	private static final String REPOSITORY_FILE_BASED = "REPOSITORY_FILE_BASED";

	private static final Logger logger = LoggerFactory.getLogger(AbstractScriptExecutor.class);
	
	/** The Constant ENGINE_TYPE_COMMAND. */
	public static final String ENGINE_TYPE = "command";
	
	/** The Constant COMMAND_EXTENSION. */
	public static final String COMMAND_EXTENSION = ".command";
	
	/** The Constant MODULE_EXT_COMMAND. */
	public static final String MODULE_EXT_COMMAND = COMMAND_EXTENSION + "/";
	
	public static final String ENGINE_NAME = "Execution Command Engine";
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return ENGINE_TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(code, executionContext, false);
	}
	
	/**
	 * Execute service.
	 *
	 * @param moduleOrCode
	 *            the module or code
	 * @param executionContext
	 *            the execution context
	 * @param isModule
	 *            the is module
	 * @return the object
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule) throws ScriptingException {
		
		logger.trace("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.trace("module or code=" + moduleOrCode); //$NON-NLS-1$

		if (moduleOrCode == null) {
			throw new ScriptingException("Command module name cannot be null");
		}
		
		if (isModule) {
			ResourcePath resourcePath = getResourcePath(moduleOrCode, MODULE_EXT_COMMAND);
			moduleOrCode = resourcePath.getModule();
			if (HttpRequestFacade.isValid()) {
				HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, resourcePath.getPath());
			}
		}

		String result = null;
		
		String commandSource = (isModule ? loadSource(moduleOrCode) : moduleOrCode);
		
		CommandDefinition commandDefinition;
		try {
			commandDefinition = GsonHelper.GSON.fromJson(commandSource, CommandDefinition.class);
		} catch (Exception e2) {
			logger.error(e2.getMessage(), e2);
			throw new ScriptingException(e2);
		}

		commandDefinition.validate();
		
		String commandLine = commandDefinition.getTargetCommand().getCommand();

		result = executeCommandLine(commandLine, commandDefinition.getSet(), commandDefinition.getUnset(), Boolean.parseBoolean(getRepository().getParameter(REPOSITORY_FILE_BASED)));

		try {
			HttpResponseFacade.setContentType(commandDefinition.getContentType());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.trace("exiting: executeServiceModule()");
		return result;
	}

	public String executeCommandLine(String commandLine, Map<String, String> forAdding, List<String> forRemoving, boolean isFileBasedRepository) throws ScriptingException {
		String result = null;

		String[] args = null;
		try {
			args = ProcessUtils.translateCommandline(commandLine);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			throw new ScriptingException(e1);
		}

		logger.debug("executing command=" + commandLine); //$NON-NLS-1$

		ByteArrayOutputStream out;
		try {
			ProcessBuilder processBuilder = ProcessUtils.createProcess(args);

			ProcessUtils.addEnvironmentVariables(processBuilder, forAdding);
			ProcessUtils.removeEnvironmentVariables(processBuilder, forRemoving);

			if (isFileBasedRepository) {
				String root = getRepository().getParameter(REPOSITORY_ROOT_FOLDER);
				processBuilder.directory(new File(root + IRepositoryStructure.PATH_REGISTRY_PUBLIC));
			}

			processBuilder.redirectErrorStream(true);

			out = new ByteArrayOutputStream();
			Process process = ProcessUtils.startProcess(args, processBuilder);
			Piper pipe = new Piper(process.getInputStream(), out);
			new Thread(pipe).start();
			try {
				int i = 0;
				boolean deadYet = false;
				do {
					Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
					try {
						process.exitValue();
						deadYet = true;
					} catch (IllegalThreadStateException e) {
						if (++i >= ProcessUtils.DEFAULT_LOOP_COUNT) {
							process.destroy();
							throw new RuntimeException(
									"Exeeds timeout - " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * ProcessUtils.DEFAULT_LOOP_COUNT));
						}
					}
				} while (!deadYet);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new IOException(e);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ScriptingException(e);
		}
		result = new String(out.toByteArray());
		return result;
	}

	private String loadSource(String module) throws ScriptingException {
		
		if (module == null) {
			throw new ScriptingException("Module location cannot be null");
		}

		byte[] sourceCode = null;
		if (module.endsWith(COMMAND_EXTENSION)) {
			sourceCode = retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC, module).getContent();
		} else {
			sourceCode = retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC, module, COMMAND_EXTENSION).getContent();
		}

		return new String(sourceCode, StandardCharsets.UTF_8);
	}


}
