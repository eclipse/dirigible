/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.engine.js.rhino.processor;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.engine.api.IBaseScriptExecutor;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;

public class RepositoryModuleSourceProvider extends ModuleSourceProviderBase {

	private static final long serialVersionUID = -5527033249080497877L;

	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$
	private static final String RHINO_EXTENSION = ".rhino"; //$NON-NLS-1$

	private IBaseScriptExecutor executor;
	
	private String rootPath;
	
	public RepositoryModuleSourceProvider(IBaseScriptExecutor executor, String rootPath) {
		this.executor = executor;
		this.rootPath = rootPath;
	}
	
	@Override
	public ModuleSource loadSource(String module, Scriptable paths, Object validator) throws IOException, URISyntaxException {

		if (module == null) {
			throw new IOException("Module location cannot be null");
		}

		ModuleSource moduleSource = null;
		if (!module.endsWith(JS_EXTENSION)) {
			module += JS_EXTENSION;
		}
		moduleSource = createModule(module);
		
		return moduleSource;
	}

	private ModuleSource createModule(String module) throws URISyntaxException {
		byte[] sourceCode;
		ModuleSource moduleSource;
		sourceCode = executor.retrieveModule(module, "", rootPath).getContent();
		moduleSource = new ModuleSource(new InputStreamReader(new ByteArrayInputStream(sourceCode), StandardCharsets.UTF_8), null,
				new URI(module), null, null);
		return moduleSource;
	}

	@Override
	protected ModuleSource loadFromUri(URI uri, URI base, Object validator) throws IOException, URISyntaxException {
		// not used
		return null;
	}

}
