/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.process.Piper;
import org.eclipse.dirigible.commons.process.ProcessUtils;
import org.eclipse.dirigible.components.base.helpers.logging.LoggingOutputStream;
import org.eclipse.dirigible.components.base.project.ProjectAction;
import org.eclipse.dirigible.components.base.project.ProjectMetadata;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class ActionsService.
 */
@Service
public class ActionsService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ActionsService.class);
	
	/** The workspace service. */
    @Autowired
    private WorkspaceService workspaceService;
	
	/**
	 * Execute action.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param action the action
	 * @return the int
	 */
	public int executeAction(String workspace, String project, String action) {
		Project projectObject = workspaceService.getProject(workspace, project);
		File fileObject = projectObject.getFile("project.json");
		if (!fileObject.exists()) {
			// no project.json file, hence no action to be executed
			return -1;
		}
		try {
			ProjectMetadata projectJson = GsonHelper.fromJson(new String(fileObject.getContent()), ProjectMetadata.class);
			List<ProjectAction> actions = projectJson.getActions();
			if (actions == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actions section not found in the project descriptor file: " + project);
			}
			Optional<ProjectAction> actionCommand = actions.stream().filter(a -> a.getName().equals(action)).findFirst();
			if (actionCommand.isPresent()) {
				String workingDirectory = LocalWorkspaceMapper.getMappedName((FileSystemRepository) projectObject.getRepository(), projectObject.getPath());
				int result = executeCommandLine(workingDirectory, actionCommand.get().getCommand());
				logger.debug("Executed project action: " + action);
				return result;
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Action not found: " + action);
			}
		} catch(Exception e ) {
			String error = "Malformed project file: " + project + " (" + e.getMessage() + ")";
			logger.error(error);
			logger.trace(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
		
	}
	
	/**
	 * List actions.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the string
	 */
	public String listActions(String workspace, String project) {
		return GsonHelper.toJson(listRegisteredActions(workspace, project));
	}
		
	/**
	 * List actions.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the list of actions
	 */
	public List<ProjectAction> listRegisteredActions(String workspace, String project) {
		Project projectObject = workspaceService.getProject(workspace, project);
		File fileObject = projectObject.getFile("project.json");
		if (!fileObject.exists()) {
			return new ArrayList<ProjectAction>();
		}
		try {
			ProjectMetadata projectJson = GsonHelper.fromJson(new String(fileObject.getContent()), ProjectMetadata.class);
			List<ProjectAction> actions = projectJson.getActions();
			if (actions == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actions section not found in the project descriptor file: " + project);
			}			
			return actions;
		} catch (Exception e) {
			String error = "Malformed project file: " + project + " (" + e.getMessage() + ")";
			logger.error(error);
			logger.trace(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
	}
	
	/**
	 * Execute command line.
	 *
	 * @param workingDirectory the working directory
	 * @param commandLine the command line
	 * @return the int
	 * @throws Exception the exception
	 */
	public int executeCommandLine(String workingDirectory, String commandLine) throws Exception {
		int result = 0;
		String[] args;
		try {
			args = ProcessUtils.translateCommandline(commandLine);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			throw new Exception(e);
		}

		try {
			ProcessBuilder processBuilder = ProcessUtils.createProcess(args);

			processBuilder.directory(new java.io.File(workingDirectory));

			processBuilder.redirectErrorStream(true);
			
			Process process = ProcessUtils.startProcess(args, processBuilder);
			Piper pipe = new Piper(process.getInputStream(), new LoggingOutputStream(logger, LoggingOutputStream.LogLevel.INFO));
			new Thread(pipe).start();
			try {
				int i = 0;
				boolean deadYet = false;
				do {
					Thread.sleep(ProcessUtils.DEFAULT_WAIT_TIME);
					try {
						result = process.exitValue();
						deadYet = true;
					} catch (IllegalThreadStateException e) {
						if (++i >= ProcessUtils.DEFAULT_LOOP_COUNT) {
							process.destroy();
							throw new RuntimeException(
									"Exceeds timeout - " + ((ProcessUtils.DEFAULT_WAIT_TIME / 1000) * ProcessUtils.DEFAULT_LOOP_COUNT));
						}
					}
				} while (!deadYet);

			} catch (Exception e) {
				if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				throw new IOException(e);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			throw new Exception(e);
		}
		return result;
	}

}
