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
package org.eclipse.dirigible.runtime.core.embed;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;

/**
 * Utility class used in cases you need an embedded version of Dirigible.
 */
public class EmbeddedDirigible {
	
	/** The Constant ENGINE_TYPE_JAVASCRIPT. */
	public static final String ENGINE_TYPE_JAVASCRIPT = "javascript";
	
	/** The repository. */
	private IRepository repository;
	
	/** The initializer. */
	private DirigibleInitializer initializer;
	
	/**
	 * Initialize the Dirigible instance.
	 *
	 * @return the dirigible initializer
	 */
	public DirigibleInitializer initialize() {
		
		this.initializer = new DirigibleInitializer();
		
		// initialize the Dirigible instance
		this.initializer.initialize();
		
		// initialize the repository object
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		
		return this.initializer;
	}
	
	/**
	 * Load the content from the file system.
	 * The folders and files under the root folder is imported to the Dirigible's Registry to be ready for execution.
	 * 
	 * @param root the root location of the content to load
	 * @throws IOException in case of an error
	 */
	public void load(File root) throws IOException {
		Path source = Paths.get(root.getAbsolutePath());
		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		Files.walkFileTree(source, opts, Integer.MAX_VALUE, new FolderToRegistryImporter(source, this.repository));
	}
	
	/**
	 * Load the content from the file system.
	 * The folders and files under the root folder is imported to the Dirigible's Registry to be ready for execution.
	 * 
	 * @param root the root location of the content to load
	 * @throws IOException in case of an error
	 */
	public void load(String root) throws IOException {
		load(new File(root));
	}
	
	/**
	 * Re-load the content from the file system - first clear the Registry content and then import again.
	 * 
	 * @param root the root location of the content to load
	 * @throws IOException in case of an error
	 */
	public void reload(File root) throws IOException {
		this.repository.removeCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		load(root);
	}
	
	/**
	 * Re-load the content from the file system - first clear the Registry content and then import again.
	 * 
	 * @param root the root location of the content to load
	 * @throws IOException in case of an error
	 */
	public void reload(String root) throws IOException {
		reload(new File(root));
	}
	
	/**
	 * Destroy the Dirigible instance.
	 */
	public void destroy() {
		this.initializer.destory();
	}
	
	/**
	 * Execute a service module with context parameters based on its type.
	 *
	 * @param engine the type of the engine e.g. 'javascript'
	 * @param module the module identifier
	 * @param context the execution context parameters
	 * @param request the HTTP request instance if any
	 * @param response the HTTP response instance if any
	 * @return the result of the execution
	 * @throws ScriptingException in case of an error
	 * @throws ContextException in case of an error
	 */
	public Object execute(String engine, String module, Map<Object, Object> context, HttpServletRequest request, HttpServletResponse response) throws ScriptingException, ContextException {
		// initialize the context
		ThreadContextFacade.setUp();
		try {
			// set the request object in a servlet context or null oterwise
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
			// set the response object in a servlet context or null oterwise
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), response);
			// execute module
			return ScriptEngineExecutorsManager.executeServiceModule(engine, module, context);
		} finally {
			ThreadContextFacade.tearDown();
		}
	}
	
	/**
	 * Execute a service module with context parameters based on its type.
	 *
	 * @param engine the type of the engine e.g. 'javascript'
	 * @param module the module identifier
	 * @param context the execution context parameters
	 * @return the result of the execution
	 * @throws ScriptingException in case of an error
	 * @throws ContextException in case of an error
	 */
	public Object execute(String engine, String module, Map<Object, Object> context) throws ScriptingException, ContextException {
		return execute(engine, module, context, null, null);
	}
	
	/**
	 * Execute a service module.
	 *
	 * @param engine the type of the engine e.g. 'javascript'
	 * @param module the module identifier
	 * @return the result of the execution
	 * @throws ScriptingException in case of an error
	 * @throws ContextException in case of an error
	 */
	public Object execute(String engine, String module) throws ScriptingException, ContextException {
		return execute(engine, module, null);
	}
	
	/**
	 * Execute a JavaScript service module.
	 *
	 * @param module the module identifier
	 * @return the result of the execution
	 * @throws ScriptingException in case of an error
	 * @throws ContextException in case of an error
	 */
	public Object executeJavaScript(String module) throws ScriptingException, ContextException {
		return execute(ENGINE_TYPE_JAVASCRIPT, module);
	}
	
	/**
	 * Execute a JavaScript service module.
	 *
	 * @param module the module identifier
	 * @param context the execution context parameters
	 * @return the result of the execution
	 * @throws ScriptingException in case of an error
	 * @throws ContextException in case of an error
	 */
	public Object executeJavaScript(String module, Map<Object, Object> context) throws ScriptingException, ContextException {
		return execute(ENGINE_TYPE_JAVASCRIPT, module, context);
	}

}
