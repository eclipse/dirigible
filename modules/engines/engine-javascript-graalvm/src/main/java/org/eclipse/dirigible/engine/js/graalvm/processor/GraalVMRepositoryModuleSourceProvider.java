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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptModuleSourceProvider;

/**
 * The GraalVM Repository Module Source Provider.
 */
public class GraalVMRepositoryModuleSourceProvider implements IJavascriptModuleSourceProvider {

	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$

	private static final String MJS_EXTENSION = ".mjs";

	private IScriptEngineExecutor executor;

	private String root;

	/**
	 * Instantiates a new GraalVM repository module source provider.
	 *
	 * @param executor
	 *            the executor
	 * @param root
	 *            the root
	 */
	public GraalVMRepositoryModuleSourceProvider(IScriptEngineExecutor executor, String root) {
		this.executor = executor;
		this.root = root;
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
	@Override
	public String loadSource(String module) throws IOException, URISyntaxException {

		if (module == null) {
			throw new IOException("Module location cannot be null");
		}

		byte[] sourceCode = null;
		if (module.endsWith(JS_EXTENSION)) {
			sourceCode = executor.retrieveModule(root, module).getContent();
		} else if (module.endsWith(MJS_EXTENSION)) {
			sourceCode = executor.retrieveModule(root, module).getContent();
		}
		else {
			sourceCode = executor.retrieveModule(root, module, JS_EXTENSION).getContent();
		}


		return new String(sourceCode, StandardCharsets.UTF_8);
	}

}
