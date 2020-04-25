/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;

/**
 * The Rhino Repository Module Source Provider.
 */
public class RhinoRepositoryModuleSourceProvider extends ModuleSourceProviderBase {

	private static final long serialVersionUID = -5527033249080497877L;

	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$

	private transient IScriptEngineExecutor executor;

	private String root;

	/**
	 * Instantiates a new rhino repository module source provider.
	 *
	 * @param executor
	 *            the executor
	 * @param root
	 *            the root
	 */
	public RhinoRepositoryModuleSourceProvider(IScriptEngineExecutor executor, String root) {
		this.executor = executor;
		this.root = root;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase#loadSource(java.lang.String,
	 * org.mozilla.javascript.Scriptable, java.lang.Object)
	 */
	@Override
	public ModuleSource loadSource(String module, Scriptable paths, Object validator) throws IOException, URISyntaxException {

		if (module == null) {
			throw new IOException("Module location cannot be null");
		}

		ModuleSource moduleSource = null;
		
		moduleSource = createModule(module);

		return moduleSource;
	}

	/**
	 * Creates the module.
	 *
	 * @param module
	 *            the module
	 * @return the module source
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	protected ModuleSource createModule(String module) throws URISyntaxException {
		byte[] sourceCode;
		ModuleSource moduleSource;
		try {
			if (!module.endsWith(JS_EXTENSION)) {
				module += JS_EXTENSION;
			}
			if (!executor.existsModule(root, module)) {
				if (module.endsWith(JS_EXTENSION)) {
					module = module.substring(0, module.length() - 3);
				}
			}
			sourceCode = executor.retrieveModule(root, module).getContent();
		} catch (RepositoryNotFoundException e) {
			throw new EcmaError(null, module, 0, 0, e.getMessage());
		}
		moduleSource = new ModuleSource(new InputStreamReader(new ByteArrayInputStream(sourceCode), StandardCharsets.UTF_8), null, new URI(module),
				null, null);
		return moduleSource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase#loadFromUri(java.net.URI,
	 * java.net.URI, java.lang.Object)
	 */
	@Override
	protected ModuleSource loadFromUri(URI uri, URI base, Object validator) throws IOException, URISyntaxException {
		// not used
		return null;
	}

}
