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
package org.eclipse.dirigible.cms.db;

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

import org.eclipse.dirigible.cms.db.dao.CmisDatabaseRepositoryDao;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryExportException;
import org.eclipse.dirigible.repository.api.RepositoryImportException;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositorySearchException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.search.RepositorySearcher;
import org.eclipse.dirigible.repository.zip.RepositoryZipExporter;
import org.eclipse.dirigible.repository.zip.RepositoryZipImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Database based Repository implementation of {@link IRepository}.
 */
public class CmsDatabaseRepository implements IRepository {

	private static final Logger logger = LoggerFactory.getLogger(CmsDatabaseRepository.class);

	/** The Constant TYPE. */
	public static final String TYPE = "database";

	private static final String PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL = "Provided Zip Input Stream cannot be null.";

	private static final String PROVIDED_ZIP_DATA_CANNOT_BE_NULL = "Provided Zip Data cannot be null.";

	private CmisDatabaseRepositoryDao databaseRepositoryDao;

	private RepositorySearcher repositorySearcher;
	
	private Map<String, String> parameters = Collections.synchronizedMap(new HashMap<>());
	
	private final AtomicLong lastModified = new AtomicLong(0);

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param datasource
	 *            the provided datasource
	 * @throws CmsDatabaseRepositoryException
	 *             in case the repository cannot be created
	 */
	public CmsDatabaseRepository(DataSource datasource) throws CmsDatabaseRepositoryException {
		this.databaseRepositoryDao = new CmisDatabaseRepositoryDao(this, datasource);
		this.repositorySearcher = new RepositorySearcher(this);
		lastModified.set(System.currentTimeMillis());
	}

	public CmisDatabaseRepositoryDao getRepositoryDao() {
		return this.databaseRepositoryDao;
	}

	/**
	 * Initializes the Database Repository
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-repository-database.properties");
		logger.trace(this.getClass().getCanonicalName() + " module initialized.");
	}

	@Override
	public ICollection getRoot() {
		logger.trace("entering getRoot"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(IRepository.SEPARATOR);
		CmsDatabaseCollection dbCollection = new CmsDatabaseCollection(this, wrapperPath);
		logger.trace("exiting getRoot"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public ICollection getCollection(String path) {
		logger.trace("entering getCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		CmsDatabaseCollection dbCollection = new CmsDatabaseCollection(this, wrapperPath);
		logger.trace("exiting getCollection"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public boolean hasCollection(String path) throws RepositoryReadException {
		logger.trace("entering hasCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new CmsDatabaseCollection(this, wrapperPath);
		boolean result = dbCollection.exists();
		logger.trace("exiting hasCollection"); //$NON-NLS-1$
		return result;
	}

	@Override
	public IResource getResource(String path) {
		logger.trace("entering getResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		CmsDatabaseResource dbResource = new CmsDatabaseResource(this, wrapperPath);
		logger.trace("exiting getResource"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public boolean hasResource(String path) throws RepositoryReadException {
		logger.trace("entering hasResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new CmsDatabaseResource(this, wrapperPath);
		boolean result = dbResource.exists();
		logger.trace("exiting hasResource"); //$NON-NLS-1$
		return result;
	}

	@Override
	public ICollection createCollection(String path) throws RepositoryWriteException {
		logger.trace("entering createCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final CmsDatabaseCollection dbCollection = new CmsDatabaseCollection(this, wrapperPath);
		dbCollection.create();
		logger.trace("exiting createCollection"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public void removeCollection(String path) throws RepositoryWriteException {
		logger.trace("entering removeCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new CmsDatabaseCollection(this, wrapperPath);
		dbCollection.delete();
		logger.trace("exiting removeCollection"); //$NON-NLS-1$
	}

	@Override
	public IResource createResource(String path) throws RepositoryWriteException {
		logger.trace("entering createResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new CmsDatabaseResource(this, wrapperPath);
		dbResource.create();
		logger.trace("exiting createResource"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public IResource createResource(String path, byte[] content) throws RepositoryWriteException {
		logger.trace("entering createResource with Content"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new CmsDatabaseResource(this, wrapperPath);
		dbResource.setContent(content);
		logger.trace("exiting createResource with Content"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return createResource(path, content, isBinary, contentType, false);
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override)
			throws RepositoryWriteException {
		logger.trace("entering createResource with Content"); //$NON-NLS-1$
		try {
			getRepositoryDao().createFile(path, content, isBinary, contentType, override);
		} catch (CmsDatabaseRepositoryException e) {
			throw new RepositoryWriteException(e);
		}
		final IResource resource = getResource(path);
		logger.trace("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public void removeResource(String path) throws RepositoryWriteException {
		logger.trace("entering removeResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new CmsDatabaseResource(this, wrapperPath);
		dbResource.delete();
		logger.trace("exiting removeResource"); //$NON-NLS-1$
	}

	@Override
	public void dispose() {
		databaseRepositoryDao.dispose();
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false);
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override) throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false, false);
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName)
			throws RepositoryImportException {
		if (zipInputStream == null) {
			logger.error(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
			throw new RepositoryImportException(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
		}
		RepositoryZipImporter.importZip(this, zipInputStream, relativeRoot, override, excludeRootFolderName);
	}

	@Override
	public void importZip(byte[] data, String relativeRoot) throws RepositoryImportException {
		importZip(data, relativeRoot, false);
	}

	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override) throws RepositoryImportException {
		importZip(data, relativeRoot, false, false, null);
	}

	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws RepositoryImportException {
		if (data == null) {
			logger.error(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
			throw new RepositoryImportException(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
		}
		RepositoryZipImporter.importZip(this, new ZipInputStream(new ByteArrayInputStream(data)), relativeRoot, override, excludeRootFolderName,
				filter);
	}

	@Override
	public byte[] exportZip(List<String> relativeRoots) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoots);
	}

	@Override
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoot, inclusive);
	}

	@Override
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchName(parameter, caseInsensitive);
	}

	@Override
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchName(root, parameter, caseInsensitive);
	}

	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchPath(parameter, caseInsensitive);
	}

	@Override
	public List<IEntity> searchText(String parameter) throws RepositorySearchException {
		List<IEntity> entities = new ArrayList<IEntity>();
		List<String> paths = repositorySearcher.search(parameter);
		for (String path : paths) {
			entities.add(new CmsDatabaseResource(this, new RepositoryPath(path)));
		}
		return entities;
	}

	@Override
	public void searchRefresh() throws RepositorySearchException {
		repositorySearcher.forceReindex();
	}

	@Override
	public List<String> getAllResourcePaths() throws RepositoryReadException {
		try {
			return databaseRepositoryDao.getAllResourcePaths();
		} catch (SQLException e) {
			throw new RepositoryReadException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepository#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	protected void setParameter(String key, String value) {
		parameters.put(key, value);
	}

	@Override
	public boolean isLinkingPathsSupported() {
		return false;
	}

	@Override
	public void linkPath(String repositoryPath, String filePath) throws IOException {
		throw new UnsupportedOperationException("Linking of external paths not supported for this Repository type");
	}

	@Override
	public boolean isLinkedPath(String repositoryPath) {
		throw new UnsupportedOperationException("Linking of external paths not supported for this Repository type");
	}
	
	@Override
	public List<String> find(String path, String pattern) throws RepositorySearchException {
		List<String> result = new ArrayList<String>();
		databaseRepositoryDao.searchName(pattern, false).forEach(e -> result.add(e.getPath()));
		return result;
	}
	
	@Override
	public long getLastModified() {
		return lastModified.get();
	}
	
	protected void setLastModified(long time) {
		lastModified.set(time);
	}
}
