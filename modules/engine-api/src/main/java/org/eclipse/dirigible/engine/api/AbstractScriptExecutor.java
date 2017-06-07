/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.engine.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScriptExecutor implements IBaseScriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(AbstractScriptExecutor.class);

	protected abstract void executeServiceModule(String module,
			Map<Object, Object> executionContext) throws ScriptingException;

	@Inject
	private IRepository repository;

	@Override
	public byte[] readResourceData(String repositoryPath) throws RepositoryException {
		final IResource resource = repository.getResource(repositoryPath);
		if (!resource.exists()) {
			final String logMsg = String.format("There is no resource [%s] at the specified Service path: %s", resource.getName(), repositoryPath);
			logger.error(logMsg);
			throw new RepositoryException(logMsg);
		}
		return resource.getContent();
	}

	@Override
	public Module retrieveModule(String module, String extension, String rootPath) throws RepositoryException {

		// try from the registry
		String resourcePath = createResourcePath(rootPath, module, extension);
		final IResource resource = repository.getResource(resourcePath);
		if (resource.exists()) {
			return new Module(getModuleName(resource.getPath()), resource.getPath(), readResourceData(resourcePath));
		}
		
		// try from the classloader
		try {
			String location = IRepository.SEPARATOR + module;
			InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream(location);
			if (bundled != null) {
				return new Module(getModuleName(location), location, IOUtils.toByteArray(bundled));
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		}

		// not found
		final String logMsg = String.format("There is no resource [%s] at the specified Service path: %s", (module + extension), rootPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
	}

	@Override
	public List<Module> retrieveModulesByExtension(String extension, String rootPath) throws RepositoryException {
		Map<String, Module> modules = new HashMap<String, Module>();
		List<IEntity> entities = repository.searchName(rootPath, "%" + extension, false);
		for (IEntity entity : entities) {
			if (entity.exists()) {
				String path = entity.getPath();
				String moduleName = getModuleName(path);
				Module module = new Module(moduleName, path, readResourceData(path), entity.getInformation());
				modules.put(moduleName, module);
			}
		}
		return Arrays.asList(modules.values().toArray(new Module[] {}));
	}

	private String getModuleName(String path) {
//		path = FilenameUtils.separatorsToUnix(path);
//		String workspace = ICommonConstants.WORKSPACE + ICommonConstants.SEPARATOR;
//		String scriptingServices = getModuleType(path) // ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES
//				+ ICommonConstants.SEPARATOR;
//		int indexOfSandbox = path.indexOf(ICommonConstants.SANDBOX);
//		int indexOfRegistry = path.indexOf(ICommonConstants.REGISTRY);
//		String result = null;
//		if ((indexOfSandbox > 0) || (indexOfRegistry > 0)) {
//			int indexOfScriptingServices = path.indexOf(scriptingServices);
//			result = path.substring(indexOfScriptingServices + scriptingServices.length());
//		} else {
//			int indexOfWorkspace = path.indexOf(workspace);
//			result = path.substring(indexOfWorkspace + workspace.length());
//			result = result.replace(scriptingServices, "");
//		}
//		return result;
		return path;
	}

	private String createResourcePath(String root, String module, String extension) {
		StringBuilder buff = new StringBuilder().append(root).append(IRepository.SEPARATOR).append(module);
		if (extension != null) {
			buff.append(extension);
		}
		String resourcePath = buff.toString();
		return resourcePath;
	}

	@Override
	public ICollection getCollection(String repositoryPath) throws RepositoryException {
		final ICollection collection = repository.getCollection(repositoryPath);
		if (!collection.exists()) {
			final String logMsg = String.format("There is no collection [%s] at the specified Service path: %s", collection.getName(), repositoryPath);
			logger.error(logMsg);
			throw new RepositoryException(logMsg);
		}
		return collection;
	}

	@Override
	public IResource getResource(String repositoryPath) throws RepositoryException {
		final IResource resource = repository.getResource(repositoryPath);
		if (!resource.exists()) {
			final String logMsg = String.format("There is no collection [%s] at the specified Service path: %s", resource.getName(), repositoryPath);
			logger.error(logMsg);
			throw new RepositoryException(logMsg);
		}
		return resource;
	}
}
