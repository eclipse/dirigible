/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.engine.js.v8.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.engine.api.script.IScriptExecutor;

public class V8RepositoryModuleSourceProvider {

	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$

	private IScriptExecutor executor;
	
	private String root;
	
	public V8RepositoryModuleSourceProvider(IScriptExecutor executor, String root) {
		this.executor = executor;
		this.root = root;
	}
	
	public String loadSource(String module) throws IOException, URISyntaxException {

		if (module == null) {
			throw new IOException("Module location cannot be null");
		}

		byte[] sourceCode = null;
		if (module.endsWith(JS_EXTENSION)) {
			sourceCode = executor.retrieveModule(root, module).getContent();
		} else {
			sourceCode = executor.retrieveModule(root, module, JS_EXTENSION).getContent();
		}

		return new String(sourceCode, StandardCharsets.UTF_8);
	}

}
