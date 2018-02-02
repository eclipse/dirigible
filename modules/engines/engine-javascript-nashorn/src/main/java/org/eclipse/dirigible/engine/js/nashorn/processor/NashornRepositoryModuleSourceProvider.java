/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;

/**
 * The Nashorn Repository Module Source Provider.
 */
public class NashornRepositoryModuleSourceProvider {

	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$

	private IScriptEngineExecutor executor;

	private String rootPath;

	/**
	 * Instantiates a new nashorn repository module source provider.
	 *
	 * @param executor
	 *            the executor
	 * @param rootPath
	 *            the root path
	 */
	public NashornRepositoryModuleSourceProvider(IScriptEngineExecutor executor, String rootPath) {
		this.executor = executor;
		this.rootPath = rootPath;
	}

	/**
	 * Load source.
	 *
	 * @param module
	 *            the module
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	public String loadSource(String module) throws IOException, URISyntaxException {

		if (module == null) {
			throw new IOException("Module location cannot be null");
		}

		byte[] sourceCode = null;
		if (module.endsWith(JS_EXTENSION)) {
			sourceCode = executor.retrieveModule(rootPath, module).getContent();
		} else {
			sourceCode = executor.retrieveModule(rootPath, module, JS_EXTENSION).getContent();
		}

		return new String(sourceCode, StandardCharsets.UTF_8);
	}

}
