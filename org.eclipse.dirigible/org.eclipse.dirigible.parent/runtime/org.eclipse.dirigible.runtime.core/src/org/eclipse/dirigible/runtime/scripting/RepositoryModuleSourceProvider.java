/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.dirigible.repository.api.IRepository;

public class RepositoryModuleSourceProvider implements ISourceProvider {

	private static final long serialVersionUID = -5527033249080497877L;

	private static final String MODULE_LOCATION_CANNOT_BE_NULL = Messages.getString("ScriptLoader.MODULE_LOCATION_CANNOT_BE_NULL"); //$NON-NLS-1$
	private static final String JS_EXTENSION = ".js"; //$NON-NLS-1$
	private static final String JSLIB_EXTENSION = ".js"; //$NON-NLS-1$

	private IBaseScriptExecutor executor;
	private IRepository repository;
	private String[] rootPaths;

	public RepositoryModuleSourceProvider(IBaseScriptExecutor executor, IRepository repository, String... rootPaths) {
		this.executor = executor;
		this.repository = repository;
		this.rootPaths = rootPaths;
	}

	@Override
	public String loadSource(String moduleId) throws IOException, URISyntaxException {

		if (moduleId == null) {
			throw new IOException(MODULE_LOCATION_CANNOT_BE_NULL);
		}

		byte[] sourceCode = null;
		if (moduleId.endsWith(JS_EXTENSION)) {
			sourceCode = executor.retrieveModule(repository, moduleId, "", rootPaths).getContent();
		} else {
			sourceCode = executor.retrieveModule(repository, moduleId, JSLIB_EXTENSION, rootPaths).getContent();
		}

		return new String(sourceCode);
	}

}
