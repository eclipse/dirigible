/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipInputStream;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryExportException;
import org.eclipse.dirigible.repository.api.RepositoryImportException;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositorySearchException;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.search.RepositorySearcher;
import org.eclipse.dirigible.repository.zip.RepositoryZipExporter;
import org.eclipse.dirigible.repository.zip.RepositoryZipImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Database based Repository implementation of {@link IRepository}.
 */
public class DatabaseRepository implements IRepository {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseRepository.class);

	/** The Constant TYPE. */
	public static final String TYPE = "database";

	/** The Constant PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL. */
	private static final String PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL = "Provided Zip Input Stream cannot be null.";

	/** The Constant PROVIDED_ZIP_DATA_CANNOT_BE_NULL. */
	private static final String PROVIDED_ZIP_DATA_CANNOT_BE_NULL = "Provided Zip Data cannot be null.";

	/** The database repository dao. */
	private DatabaseRepositoryDao databaseRepositoryDao;

	/** The repository searcher. */
	private RepositorySearcher repositorySearcher;
	
	/** The parameters. */
	private Map<String, String> parameters = Collections.synchronizedMap(new HashMap<>());
	
	/** The last modified. */
	private final AtomicLong lastModified = new AtomicLong(0);

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param datasource
	 *            the provided datasource
	 * @throws DatabaseRepositoryException
	 *             in case the repository cannot be created
	 */
	public DatabaseRepository(DataSource datasource) throws DatabaseRepositoryException {
		this.databaseRepositoryDao = new DatabaseRepositoryDao(this, datasource);
		this.repositorySearcher = new RepositorySearcher(this);
		lastModified.set(System.currentTimeMillis());
	}

	/**
	 * Gets the repository dao.
	 *
	 * @return the repository dao
	 */
	public DatabaseRepositoryDao getRepositoryDao() {
		return this.databaseRepositoryDao;
	}

	/**
	 * Initializes the Database Repository.
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-repository-database.properties");
		if (logger.isTraceEnabled()) {logger.trace(this.getClass().getCanonicalName() + " module initialized.");}
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	@Override
	public ICollection getRoot() {
		if (logger.isTraceEnabled()) {logger.trace("entering getRoot");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(IRepository.SEPARATOR);
		DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		if (logger.isTraceEnabled()) {logger.trace("exiting getRoot");} //$NON-NLS-1$
		return dbCollection;
	}

	/**
	 * Gets the collection.
	 *
	 * @param path the path
	 * @return the collection
	 */
	@Override
	public ICollection getCollection(String path) {
		if (logger.isTraceEnabled()) {logger.trace("entering getCollection");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		if (logger.isTraceEnabled()) {logger.trace("exiting getCollection");} //$NON-NLS-1$
		return dbCollection;
	}

	/**
	 * Checks for collection.
	 *
	 * @param path the path
	 * @return true, if successful
	 * @throws RepositoryReadException the repository read exception
	 */
	@Override
	public boolean hasCollection(String path) throws RepositoryReadException {
		if (logger.isTraceEnabled()) {logger.trace("entering hasCollection");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new DatabaseCollection(this, wrapperPath);
		boolean result = dbCollection.exists();
		if (logger.isTraceEnabled()) {logger.trace("exiting hasCollection");} //$NON-NLS-1$
		return result;
	}

	/**
	 * Gets the resource.
	 *
	 * @param path the path
	 * @return the resource
	 */
	@Override
	public IResource getResource(String path) {
		if (logger.isTraceEnabled()) {logger.trace("entering getResource");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		DatabaseResource dbResource = new DatabaseResource(this, wrapperPath);
		if (logger.isTraceEnabled()) {logger.trace("exiting getResource");} //$NON-NLS-1$
		return dbResource;
	}

	/**
	 * Checks for resource.
	 *
	 * @param path the path
	 * @return true, if successful
	 * @throws RepositoryReadException the repository read exception
	 */
	@Override
	public boolean hasResource(String path) throws RepositoryReadException {
		if (logger.isTraceEnabled()) {logger.trace("entering hasResource");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		boolean result = dbResource.exists();
		if (logger.isTraceEnabled()) {logger.trace("exiting hasResource");} //$NON-NLS-1$
		return result;
	}

	/**
	 * Creates the collection.
	 *
	 * @param path the path
	 * @return the i collection
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public ICollection createCollection(String path) throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering createCollection");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		dbCollection.create();
		if (logger.isTraceEnabled()) {logger.trace("exiting createCollection");} //$NON-NLS-1$
		return dbCollection;
	}

	/**
	 * Removes the collection.
	 *
	 * @param path the path
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public void removeCollection(String path) throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering removeCollection");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new DatabaseCollection(this, wrapperPath);
		dbCollection.delete();
		if (logger.isTraceEnabled()) {logger.trace("exiting removeCollection");} //$NON-NLS-1$
	}

	/**
	 * Creates the resource.
	 *
	 * @param path the path
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public IResource createResource(String path) throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering createResource");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.create();
		if (logger.isTraceEnabled()) {logger.trace("exiting createResource");} //$NON-NLS-1$
		return dbResource;
	}

	/**
	 * Creates the resource.
	 *
	 * @param path the path
	 * @param content the content
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public IResource createResource(String path, byte[] content) throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering createResource with Content");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.setContent(content);
		if (logger.isTraceEnabled()) {logger.trace("exiting createResource with Content");} //$NON-NLS-1$
		return dbResource;
	}

	/**
	 * Creates the resource.
	 *
	 * @param path the path
	 * @param content the content
	 * @param isBinary the is binary
	 * @param contentType the content type
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return createResource(path, content, isBinary, contentType, false);
	}

	/**
	 * Creates the resource.
	 *
	 * @param path the path
	 * @param content the content
	 * @param isBinary the is binary
	 * @param contentType the content type
	 * @param override the override
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override)
			throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering createResource with Content");} //$NON-NLS-1$
		try {
			getRepositoryDao().createFile(path, content, isBinary, contentType, override);
		} catch (DatabaseRepositoryException e) {
			throw new RepositoryWriteException(e);
		}
		final IResource resource = getResource(path);
		if (logger.isTraceEnabled()) {logger.trace("exiting createResource with Content");} //$NON-NLS-1$
		return resource;
	}

	/**
	 * Removes the resource.
	 *
	 * @param path the path
	 * @throws RepositoryWriteException the repository write exception
	 */
	@Override
	public void removeResource(String path) throws RepositoryWriteException {
		if (logger.isTraceEnabled()) {logger.trace("entering removeResource");} //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.delete();
		if (logger.isTraceEnabled()) {logger.trace("exiting removeResource");} //$NON-NLS-1$
	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		databaseRepositoryDao.dispose();
	}

	/**
	 * Import zip.
	 *
	 * @param zipInputStream the zip input stream
	 * @param relativeRoot the relative root
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false);
	}

	/**
	 * Import zip.
	 *
	 * @param zipInputStream the zip input stream
	 * @param relativeRoot the relative root
	 * @param override the override
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override) throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false, false);
	}

	/**
	 * Import zip.
	 *
	 * @param zipInputStream the zip input stream
	 * @param relativeRoot the relative root
	 * @param override the override
	 * @param excludeRootFolderName the exclude root folder name
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName)
			throws RepositoryImportException {
		if (zipInputStream == null) {
			if (logger.isErrorEnabled()) {logger.error(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);}
			throw new RepositoryImportException(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
		}
		RepositoryZipImporter.importZip(this, zipInputStream, relativeRoot, override, excludeRootFolderName);
	}

	/**
	 * Import zip.
	 *
	 * @param data the data
	 * @param relativeRoot the relative root
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(byte[] data, String relativeRoot) throws RepositoryImportException {
		importZip(data, relativeRoot, false);
	}

	/**
	 * Import zip.
	 *
	 * @param data the data
	 * @param relativeRoot the relative root
	 * @param override the override
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override) throws RepositoryImportException {
		importZip(data, relativeRoot, false, false, null);
	}

	/**
	 * Import zip.
	 *
	 * @param data the data
	 * @param relativeRoot the relative root
	 * @param override the override
	 * @param excludeRootFolderName the exclude root folder name
	 * @param filter the filter
	 * @throws RepositoryImportException the repository import exception
	 */
	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws RepositoryImportException {
		if (data == null) {
			if (logger.isErrorEnabled()) {logger.error(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);}
			throw new RepositoryImportException(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
		}
		RepositoryZipImporter.importZip(this, new ZipInputStream(new ByteArrayInputStream(data)), relativeRoot, override, excludeRootFolderName, filter);
	}

	/**
	 * Export zip.
	 *
	 * @param relativeRoots the relative roots
	 * @return the byte[]
	 * @throws RepositoryExportException the repository export exception
	 */
	@Override
	public byte[] exportZip(List<String> relativeRoots) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoots);
	}

	/**
	 * Export zip.
	 *
	 * @param relativeRoot the relative root
	 * @param inclusive the inclusive
	 * @return the byte[]
	 * @throws RepositoryExportException the repository export exception
	 */
	@Override
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoot, inclusive);
	}

	/**
	 * Search name.
	 *
	 * @param parameter the parameter
	 * @param caseInsensitive the case insensitive
	 * @return the list
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchName(parameter, caseInsensitive);
	}

	/**
	 * Search name.
	 *
	 * @param root the root
	 * @param parameter the parameter
	 * @param caseInsensitive the case insensitive
	 * @return the list
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchName(root, parameter, caseInsensitive);
	}

	/**
	 * Search path.
	 *
	 * @param parameter the parameter
	 * @param caseInsensitive the case insensitive
	 * @return the list
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchPath(parameter, caseInsensitive);
	}
	
	/**
	 * Find.
	 *
	 * @param path the path
	 * @param pattern the pattern
	 * @return the list
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public List<String> find(String path, String pattern) throws RepositorySearchException {
		List<String> result = new ArrayList<String>();
		databaseRepositoryDao.searchName(pattern, false).forEach(e -> result.add(e.getPath()));
		return result;
	}

	/**
	 * Search text.
	 *
	 * @param parameter the parameter
	 * @return the list
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public List<IEntity> searchText(String parameter) throws RepositorySearchException {
		List<IEntity> entities = new ArrayList<IEntity>();
		List<String> paths = repositorySearcher.search(parameter);
		for (String path : paths) {
			entities.add(new DatabaseResource(this, new RepositoryPath(path)));
		}
		return entities;
	}

	/**
	 * Search refresh.
	 *
	 * @throws RepositorySearchException the repository search exception
	 */
	@Override
	public void searchRefresh() throws RepositorySearchException {
		repositorySearcher.forceReindex();
	}

	/**
	 * Gets the all resource paths.
	 *
	 * @return the all resource paths
	 * @throws RepositoryReadException the repository read exception
	 */
	@Override
	public List<String> getAllResourcePaths() throws RepositoryReadException {
		try {
			return databaseRepositoryDao.getAllResourcePaths();
		} catch (SQLException e) {
			throw new RepositoryReadException(e);
		}
	}
	
	/**
	 * Gets the parameter.
	 *
	 * @param key the key
	 * @return the parameter
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepository#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	/**
	 * Sets the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	protected void setParameter(String key, String value) {
		parameters.put(key, value);
	}

	/**
	 * Checks if is linking paths supported.
	 *
	 * @return true, if is linking paths supported
	 */
	@Override
	public boolean isLinkingPathsSupported() {
		return false;
	}

	/**
	 * Link path.
	 *
	 * @param repositoryPath the repository path
	 * @param filePath the file path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void linkPath(String repositoryPath, String filePath) throws IOException {
		throw new UnsupportedOperationException("Linking of external paths not supported for this Repository type");
	}

	/**
	 * Delete linked path.
	 *
	 * @param repositoryPath the repository path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void deleteLinkedPath(String repositoryPath) throws IOException {
		throw new UnsupportedOperationException("Linking of external paths not supported for this Repository type");
	}

	/**
	 * Checks if is linked path.
	 *
	 * @param repositoryPath the repository path
	 * @return true, if is linked path
	 */
	@Override
	public boolean isLinkedPath(String repositoryPath) {
		throw new UnsupportedOperationException("Linking of external paths not supported for this Repository type");
	}
	
	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	@Override
	public long getLastModified() {
		return lastModified.get();
	}
	
	/**
	 * Sets the last modified.
	 *
	 * @param time the new last modified
	 */
	protected void setLastModified(long time) {
		lastModified.set(time);
	}
}
