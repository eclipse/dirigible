/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.conf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Configuration Store Facade backed by the Repository
 */
public class ConfigurationStore implements IConfigurationStore {

	private static final String REPOSITORY_OBJECT_IS_NULL_WHEN_SETTING_PROPERTIES = "Repository object is null, when setting properties"; //$NON-NLS-1$

	private static final String PROPERTIES_EXT = ".properties"; //$NON-NLS-1$

	private IRepository repository;

	/**
	 * The constructor
	 *
	 * @param repository
	 */
	public ConfigurationStore(IRepository repository) {
		this.repository = repository;
	}

	@Override
	public Properties getGlobalSettings(String path, String name) throws IOException {
		return getSettingsByRoot(IRepositoryPaths.CONF_REGISTRY, path, name);
	}

	@Override
	public byte[] getGlobalSettingsAsBytes(String path, String name) throws IOException {
		return getSettingsByRootAsBytes(IRepositoryPaths.CONF_REGISTRY, path, name);
	}

	@Override
	public void setGlobalSettings(String path, String name, Properties properties) throws IOException {
		setSettingsByRoot(IRepositoryPaths.CONF_REGISTRY, path, name, properties);
	}

	@Override
	public void setGlobalSettingsAsBytes(String path, String name, byte[] bytes) throws IOException {
		setSettingsByRootAsBytes(IRepositoryPaths.CONF_REGISTRY, path, name, bytes);
	}

	@Override
	public Properties getUserSettings(String path, String name, String userName) throws IOException {
		String root = getUserPath(userName);
		return getSettingsByRoot(root, path, name);
	}

	@Override
	public byte[] getUserSettingsAsBytes(String path, String name, String userName) throws IOException {
		String root = getUserPath(userName);
		return getSettingsByRootAsBytes(root, path, name);
	}

	@Override
	public void setUserSettings(String path, String name, Properties properties, String userName) throws IOException {
		String root = getUserPath(userName);
		setSettingsByRoot(root, path, name, properties);
	}

	@Override
	public void setUserSettingsAsBytes(String path, String name, byte[] bytes, String userName) throws IOException {
		String root = getUserPath(userName);
		setSettingsByRootAsBytes(root, path, name, bytes);
	}

	private String getUserPath(String userName) {
		return IRepositoryPaths.DB_DIRIGIBLE_USERS + userName + IRepositoryPaths.CONF_FOLDER_NAME + IRepository.SEPARATOR;
	}

	private Properties getSettingsByRoot(String root, String path, String name) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		if ((repository != null) && repository.hasResource(resourcePath)) {
			IResource resource = repository.getResource(resourcePath);
			Properties properties = new Properties();
			properties.load(new ByteArrayInputStream(resource.getContent()));
			return properties;
		}
		return new Properties();
	}

	private byte[] getSettingsByRootAsBytes(String root, String path, String name) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		if ((repository != null) && repository.hasResource(resourcePath)) {
			IResource resource = repository.getResource(resourcePath);
			return resource.getContent();
		}
		return new byte[] {};
	}

	private void setSettingsByRoot(String root, String path, String name, Properties properties) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		IResource resource = null;
		if (repository != null) {
			if (repository.hasResource(resourcePath)) {
				resource = repository.getResource(resourcePath);
			} else {
				resource = repository.createResource(resourcePath);
			}
		} else {
			throw new IOException(REPOSITORY_OBJECT_IS_NULL_WHEN_SETTING_PROPERTIES);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		properties.store(baos, resource.getPath());

		resource.setContent(baos.toByteArray());
	}

	private String normalizePath(String path) {
		if (path != null) {
			if (!path.startsWith(IRepository.SEPARATOR)) {
				String normalizedPath = IRepository.SEPARATOR + path;
				return normalizedPath;
			}
		}
		return path;
	}

	private void setSettingsByRootAsBytes(String root, String path, String name, byte[] bytes) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		IResource resource = null;
		if ((repository != null) && repository.hasResource(resourcePath)) {
			resource = repository.getResource(resourcePath);
		} else {
			resource = repository.createResource(resourcePath);
		}

		resource.setContent(bytes);
	}

	@Override
	public List<String> listGlobalSettingsNames(String path) throws IOException {
		return listSettingsByRoot(IRepositoryPaths.CONF_REGISTRY, path);
	}

	@Override
	public List<String> listUserSettingsNames(String path, String userName) throws IOException {
		String root = getUserPath(userName);
		return listSettingsByRoot(root, path);
	}

	private List<String> listSettingsByRoot(String root, String path) throws IOException {
		String normalizedPath = normalizePath(path);
		String collectionPath = root + normalizedPath;
		if ((repository != null) && repository.hasCollection(collectionPath)) {
			ICollection collection = repository.getCollection(collectionPath);
			return collection.getResourcesNames();
		}
		return new ArrayList<String>();
	}

	@Override
	public void removeGlobalSettings(String path, String name) throws IOException {
		removeSettingsByRoot(IRepositoryPaths.CONF_REGISTRY, path, name);
	}

	@Override
	public void removeUserSettings(String path, String userName, String name) throws IOException {
		String root = getUserPath(userName);
		removeSettingsByRoot(root, path, name);
	}

	public void removeSettingsByRoot(String root, String path, String name) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		if ((repository != null) && repository.hasResource(resourcePath)) {
			repository.removeResource(resourcePath);
		}
	}

	@Override
	public boolean existsGlobalSettings(String path, String name) throws IOException {
		return existsSettingsByRoot(IRepositoryPaths.CONF_REGISTRY, path, name);
	}

	@Override
	public boolean existsUserSettings(String path, String userName, String name) throws IOException {
		String root = getUserPath(userName);
		return existsSettingsByRoot(root, path, name);
	}

	/**
	 * Checks whether the setting with this path and name exists in the root's space
	 *
	 * @param root
	 * @param path
	 * @param name
	 * @return true if exists and false otherwise
	 * @throws IOException
	 */
	public boolean existsSettingsByRoot(String root, String path, String name) throws IOException {
		String normalizedPath = normalizePath(path);
		String resourcePath = root + normalizedPath + IRepository.SEPARATOR + name + PROPERTIES_EXT;
		if ((repository != null) && repository.hasResource(resourcePath)) {
			return true;
		}
		return false;
	}
}
