/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.rhino;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.runtime.scripting.IBaseScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.Messages;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;

public class RepositoryModuleSourceProvider extends ModuleSourceProviderBase {

	private static final long serialVersionUID = -5527033249080497877L;

	private static final String MODULE_LOCATION_CANNOT_BE_NULL = Messages.getString("ScriptLoader.MODULE_LOCATION_CANNOT_BE_NULL"); //$NON-NLS-1$
	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$
	private static final String JSLIB_EXTENSION = ".js"; //$NON-NLS-1$

	private IBaseScriptExecutor executor;
	private IRepository repository;
	private String[] rootPaths;

	public RepositoryModuleSourceProvider(IBaseScriptExecutor executor, IRepository repository, String... rootPaths) {
		super();
		this.executor = executor;
		this.repository = repository;
		this.rootPaths = rootPaths;
	}

	@Override
	public ModuleSource loadSource(String moduleId, Scriptable paths, Object validator) throws IOException, URISyntaxException {

		if (moduleId == null) {
			throw new IOException(MODULE_LOCATION_CANNOT_BE_NULL);
		}

		byte[] sourceCode = null;
		ModuleSource moduleSource = null;
		if (moduleId.endsWith(JS_EXTENSION)) {
			sourceCode = executor.retrieveModule(repository, moduleId, "", rootPaths).getContent();
			moduleSource = new ModuleSource(new InputStreamReader(new ByteArrayInputStream(sourceCode), StandardCharsets.UTF_8), null,
					new URI(moduleId), null, null);
		} else {
			sourceCode = executor.retrieveModule(repository, moduleId, JSLIB_EXTENSION, rootPaths).getContent();
			moduleSource = new ModuleSource(new InputStreamReader(new ByteArrayInputStream(sourceCode)), null, new URI(moduleId + JSLIB_EXTENSION),
					null, null);
		}

		return moduleSource;
	}

	@Override
	protected ModuleSource loadFromUri(URI uri, URI base, Object validator) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

}
