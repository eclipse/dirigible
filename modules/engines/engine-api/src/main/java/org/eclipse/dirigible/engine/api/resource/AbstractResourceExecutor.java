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
package org.eclipse.dirigible.engine.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Abstract Resource Executor.
 */
public abstract class AbstractResourceExecutor implements IResourceExecutor {

	private static final String LOCATION_META_INF_DIRIGIBLE = "/META-INF/dirigible";
	
	private static final String LOCATION_META_INF_WEBJARS = "/META-INF/resources/webjars";
	

	private static final Logger logger = LoggerFactory.getLogger(AbstractResourceExecutor.class);

	private IRepository repository = null;

	private static Map<String, byte[]> PREDELIVERED = Collections.synchronizedMap(new HashMap<String, byte[]>());
	

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#getResourceContent(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public byte[] getResourceContent(String root, String module) throws RepositoryException {
		return getResourceContent(root, module, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#getResourceContent(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] getResourceContent(String root, String module, String extension) throws RepositoryException {
		
		byte[] result = null;
		
		if ((module == null) || "".equals(module.trim())) {
			throw new RepositoryException("Module name cannot be empty or null.");
		}
		if (module.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
			throw new RepositoryException("Module name cannot point to a collection.");
		}
		
		// try from repository
		result = tryFromRepositoryLocation(root, module, extension);
		if (result == null) {
			// try from the classloader - dirigible
			result = tryFromDirigibleLocation(module, extension);
			if (result == null) {
				// try from the classloader - webjars
				result = tryFromWebJarsLocation(module, extension);
			}
		}
		
		if (result != null) {
			return result;
		}

		String repositoryPath = createResourcePath(root, module, extension);
		final String logMsg = String.format("There is no resource at the specified path: %s", repositoryPath);
		logger.error(logMsg);
		throw new RepositoryNotFoundException(logMsg);
	}

	private byte[] tryFromRepositoryLocation(String root, String module, String extension) {
		byte[] result = null;
		String repositoryPath = createResourcePath(root, module, extension);
		final IResource resource = getRepository().getResource(repositoryPath);
		if (resource.exists()) {
			result = resource.getContent();
		}
		return result;
	}
	
	private byte[] tryFromDirigibleLocation(String module, String extension) {
		return tryFromClassloaderLocation(module, extension, LOCATION_META_INF_DIRIGIBLE);
	}
	
	private byte[] tryFromWebJarsLocation(String module, String extension) {
		return tryFromClassloaderLocation(module, extension, LOCATION_META_INF_WEBJARS);
	}
	
	private byte[] tryFromClassloaderLocation(String module, String extension, String path) {
		byte[] result = null;
		try {
			String prefix = Character.toString(module.charAt(0)).equals(IRepository.SEPARATOR) ? "" : IRepository.SEPARATOR;
			String location = prefix + module + (extension != null ? extension : "");
			byte[] content = PREDELIVERED.get(location);
			if (content != null) {
				return content;
			}
			InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream(path + location);
			try {
				if (bundled != null) {
					content = IOUtils.toByteArray(bundled);
					PREDELIVERED.put(location, content);
					result = content;
				} 
			} finally {
				if (bundled != null) {
					bundled.close();
				}
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#getCollection(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ICollection getCollection(String root, String module) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module);
		final ICollection collection = getRepository().getCollection(repositoryPath);
		if (collection.exists()) {
			return collection;
		}

		final String logMsg = String.format("There is no collection [%s] at the specified Service path: %s", collection.getName(), repositoryPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#getResource(java.lang.String, java.lang.String)
	 */
	@Override
	public IResource getResource(String root, String module) throws RepositoryException {
		return getResource(root, module, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#getResource(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IResource getResource(String root, String module, String extension) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module, extension);
		final IResource resource = getRepository().getResource(repositoryPath);
		if (resource.exists()) {
			return resource;
		}

		final String logMsg = String.format("There is no collection [%s] at the specified path: %s", resource.getName(), repositoryPath);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#existResource(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean existResource(String root, String module) throws RepositoryException {
		return existResource(root, module, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#existResource(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean existResource(String root, String module, String extension) throws RepositoryException {
		String repositoryPath = createResourcePath(root, module, extension);
		final IResource resource = getRepository().getResource(repositoryPath);
		return resource.exists();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#createResourcePath(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String createResourcePath(String root, String module) {
		return createResourcePath(root, module, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.resource.IResourceExecutor#createResourcePath(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String createResourcePath(String root, String module, String extension) {
		StringBuilder buff = new StringBuilder().append(root);
		if (!Character.toString(module.charAt(0)).equals(IRepository.SEPARATOR)) {
			buff.append(IRepository.SEPARATOR);
		}
		buff.append(module);
		if (extension != null) {
			buff.append(extension);
		}
		String resourcePath = buff.toString();
		return resourcePath;
	}
	
	protected byte[] getLoadedPredeliveredContent(String location) {
		return PREDELIVERED.get(location);
	}

}
