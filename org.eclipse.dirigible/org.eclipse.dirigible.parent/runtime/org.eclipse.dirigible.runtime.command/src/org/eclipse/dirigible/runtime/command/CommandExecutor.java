/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.command.Piper;
import org.eclipse.dirigible.repository.ext.command.ProcessUtils;
import org.eclipse.dirigible.repository.ext.utils.FileUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.filter.XSSUtils;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;

public class CommandExecutor extends AbstractScriptExecutor {

	private static final String WORK = "work";

	private static final String COMMAND_MODULE_NAME_CANNOT_BE_NULL = Messages.getString("CommandExecutor.COMMAND_MODULE_NAME_CANNOT_BE_NULL"); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(CommandExecutor.class);

	private IRepository repository;
	private String[] rootPaths;

	public CommandExecutor(IRepository repository, String... rootPaths) {
		super();
		logger.debug("entering: constructor()");
		this.repository = repository;
		this.rootPaths = rootPaths;
		if ((this.rootPaths == null) || (this.rootPaths.length == 0)) {
			this.rootPaths = new String[] { null, null };
		}
		logger.debug("exiting: constructor()");
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.debug("module=" + module); //$NON-NLS-1$

		if (module == null) {
			throw new IOException(COMMAND_MODULE_NAME_CANNOT_BE_NULL);
		}

		String result = null;
		String commandSource = new String(retrieveModule(repository, module, "", rootPaths).getContent());

		CommandData commandData;
		try {
			commandData = CommandDataParser.parseCommandData(commandSource);
		} catch (IllegalArgumentException e2) {
			logger.error(e2.getMessage(), e2);
			throw new IOException(e2);
		}

		String commandLine = commandData.getTargetCommand().getCommand();

		String[] args = null;
		try {
			args = ProcessUtils.translateCommandline(commandLine);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			throw new IOException(e1);
		}

		logger.debug("executing command=" + commandLine); //$NON-NLS-1$

		ByteArrayOutputStream out;
		try {
			ProcessBuilder processBuilder = ProcessUtils.createProcess(args);

			ProcessUtils.addEnvironmentVariables(processBuilder, commandData.getEnvAdd());
			ProcessUtils.removeEnvironmentVariables(processBuilder, commandData.getEnvRemove());

			if (commandData.isUseContent()) {
				if (commandData.getWorkDir() == null) {
					commandData.setWorkDir(WORK);
				}
				String directory = XSSUtils.stripXSS(commandData.getWorkDir());

				if ((directory == null) || "".equals(directory)) {
					directory = WORK;
				}
				File targetFolder = FileUtils.createTempDirectory(directory);

				for (int i = rootPaths.length - 1; i >= 0; i--) {
					ICollection collection = getCollection(repository, rootPaths[i]);
					FileUtils.copyCollectionToDirectory(collection, targetFolder, rootPaths);
				}

				processBuilder.directory(targetFolder);

			} else {
				processBuilder.directory(new File(commandData.getWorkDir()));
			}
			processBuilder.redirectErrorStream(true);

			out = new ByteArrayOutputStream();
			Process process = ProcessUtils.startProcess(args, processBuilder);
			Piper pipe = new Piper(process.getInputStream(), out);
			new Thread(pipe).start();
			try {
				// process.waitFor();

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
			throw new IOException(e);
		}
		result = new String(out.toByteArray());

		response.getWriter().write(result);
		response.getWriter().flush();
		response.getWriter().close();

		logger.debug("exiting: executeServiceModule()");
		return result;
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		//
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

}
