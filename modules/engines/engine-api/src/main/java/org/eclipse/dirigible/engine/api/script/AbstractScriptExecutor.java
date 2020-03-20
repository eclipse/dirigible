/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.api.script;

import java.io.InputStream;

import org.eclipse.dirigible.engine.api.resource.AbstractResourceExecutor;
import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;

/**
 * The Abstract Script Executor.
 */
public abstract class AbstractScriptExecutor extends AbstractResourceExecutor implements IScriptEngineExecutor {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#retrieveModule(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Module retrieveModule(String root, String module) throws RepositoryException {
		return retrieveModule(root, module, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#retrieveModule(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Module retrieveModule(String root, String module, String extension) throws RepositoryException {
		String resourcePath = createResourcePath(root, module, extension);
		return new Module(resourcePath, getResourceContent(root, module, extension));
	}
	
	/**
	 * Gets the resource path.
	 *
	 * @param module
	 *            the module
	 * @param moduleExtensions
	 *            the module extensions
	 * @return the resource path
	 */
	protected ResourcePath getResourcePath(String module, String... moduleExtensions) {
		return generateResourcePath(module, moduleExtensions);
	}

	/**
	 * Generate resource path.
	 *
	 * @param module
	 *            the module
	 * @param moduleExtensions
	 *            the module extensions
	 * @return the resource path
	 */
	public static ResourcePath generateResourcePath(String module, String[] moduleExtensions) {
		for (String moduleExtension : moduleExtensions) {
			if (module.indexOf(moduleExtension) > 0) {
				ResourcePath resourcePath = new ResourcePath();
				String modulePath = module.substring(0, ((module.indexOf(moduleExtension) + moduleExtension.length()) - 1));
				resourcePath.setModule(modulePath);
				if (module.length() > modulePath.length()) {
					resourcePath.setPath(module.substring(modulePath.length() + 1));
				} else {
					resourcePath.setPath("");
				}
				return resourcePath;
			}

		}
		return new ResourcePath(module, "");
	}
	
	/**
	 * Exists module
	 * 
	 * @param root the root path
	 * @param module the module path
	 * @return true if module exists
	 * @throws RepositoryException
	 */
	public boolean existsModule(String root, String module) throws RepositoryException {
		return existsModule(root, module, null);
	}
	/**
	 * Exists module
	 * 
	 * @param root the root path
	 * @param module the module path
	 * @param extension the extension or null
	 * @return true if module exists
	 * @throws RepositoryException
	 */
	public boolean existsModule(String root, String module, String extension) throws RepositoryException {
		if (super.existResource(root, module, extension)) {
			return true;
		}
		
		String ext = extension != null ? extension : "";
		InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream(IRepository.SEPARATOR + module + ext);
		return bundled != null;
	}

}
