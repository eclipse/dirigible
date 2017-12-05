/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.db;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
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
import org.eclipse.dirigible.repository.db.dao.DatabaseRepositoryDao;
import org.eclipse.dirigible.repository.zip.RepositoryZipExporter;
import org.eclipse.dirigible.repository.zip.RepositoryZipImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based Local Repository implementation of {@link IRepository}.
 */
public class DatabaseRepository implements IRepository {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseRepository.class);

	/** The Constant TYPE. */
	public static final String TYPE = "database";

	private static final String PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL = "Provided Zip Input Stream cannot be null.";
	
	private static final String PROVIDED_ZIP_DATA_CANNOT_BE_NULL = "Provided Zip Data cannot be null.";
	
	@Inject
	private DatabaseRepositoryDao databaseRepositoryDao;

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @throws DatabaseRepositoryException
	 *             in case the repository cannot be created
	 */
	public DatabaseRepository() throws DatabaseRepositoryException {
	}
	
	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param datasource the provided datasource
	 * @throws DatabaseRepositoryException
	 *             in case the repository cannot be created
	 */
	public DatabaseRepository(DataSource datasource) throws DatabaseRepositoryException {
		this.databaseRepositoryDao = new DatabaseRepositoryDao(this, datasource);
	}

	/**
	 * Initializes the Database Repository
	 */
	public void initialize() {
		Configuration.load("/dirigible-repository-database.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}
	
	@Override
	public ICollection getRoot() {
		logger.trace("entering getRoot"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(IRepository.SEPARATOR);
		DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		logger.trace("exiting getRoot"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public ICollection getCollection(String path) {
		logger.debug("entering getCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		logger.debug("exiting getCollection"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public boolean hasCollection(String path) throws RepositoryReadException {
		logger.debug("entering hasCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new DatabaseCollection(this, wrapperPath);
		boolean result = dbCollection.exists();
		logger.debug("exiting hasCollection"); //$NON-NLS-1$
		return result;
	}

	@Override
	public IResource getResource(String path) {
		logger.debug("entering getResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		DatabaseResource dbResource = new DatabaseResource(this, wrapperPath);
		logger.debug("exiting getResource"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public boolean hasResource(String path) throws RepositoryReadException {
		logger.debug("entering hasResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		boolean result = dbResource.exists();
		logger.debug("exiting hasResource"); //$NON-NLS-1$
		return result;
	}

	@Override
	public ICollection createCollection(String path) throws RepositoryWriteException {
		logger.debug("entering createCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final DatabaseCollection dbCollection = new DatabaseCollection(this, wrapperPath);
		dbCollection.create();
		logger.debug("exiting createCollection"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public void removeCollection(String path) throws RepositoryWriteException {
		logger.debug("entering removeCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection dbCollection = new DatabaseCollection(this, wrapperPath);
		dbCollection.delete();
		logger.debug("exiting removeCollection"); //$NON-NLS-1$		
	}

	@Override
	public IResource createResource(String path) throws RepositoryWriteException {
		logger.debug("entering createResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.create();
		logger.debug("exiting createResource"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public IResource createResource(String path, byte[] content) throws RepositoryWriteException {
		logger.debug("entering createResource with Content"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.setContent(content);
		logger.debug("exiting createResource with Content"); //$NON-NLS-1$
		return dbResource;
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType)
			throws RepositoryWriteException {
		return createResource(path, content, isBinary, contentType, false);
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override)
			throws RepositoryWriteException {
		logger.debug("entering createResource with Content"); //$NON-NLS-1$
		try {
			getRepositoryDao().createFile(path, content, isBinary, contentType, override);
		} catch (DatabaseRepositoryException e) {
			throw new RepositoryWriteException(e);
		}
		final IResource resource = getResource(path);
		logger.debug("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public void removeResource(String path) throws RepositoryWriteException {
		logger.debug("entering removeResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource dbResource = new DatabaseResource(this, wrapperPath);
		dbResource.delete();
		logger.debug("exiting removeResource"); //$NON-NLS-1$
	}

	@Override
	public void dispose() {
		databaseRepositoryDao.dispose();
	}

	@Override
	public void cleanupOldVersions() throws RepositoryWriteException {
		databaseRepositoryDao.cleanupOldVersions();		
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot) throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false);		
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override)
			throws RepositoryImportException {
		importZip(zipInputStream, relativeRoot, false, false);		
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override,
			boolean excludeRootFolderName) throws RepositoryImportException {
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
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName,
			Map<String, String> filter) throws RepositoryImportException {
		if (data == null) {
			logger.error(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
			throw new RepositoryImportException(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
		}
		RepositoryZipImporter.importZip(this, new ZipInputStream(new ByteArrayInputStream(data)), relativeRoot, override, excludeRootFolderName, filter);
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
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive)
			throws RepositorySearchException {
		return databaseRepositoryDao.searchName(root, caseInsensitive);
	}

	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchPath(parameter, caseInsensitive);
	}

	@Override
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return databaseRepositoryDao.searchText(parameter, caseInsensitive);
	}

	@Override
	public List<IResourceVersion> getResourceVersions(String path) throws RepositoryVersioningException {
		return databaseRepositoryDao.getResourceVersionsByPath(path);
	}

	@Override
	public IResourceVersion getResourceVersion(String path, int version) throws RepositoryVersioningException {
		return new DatabaseResourceVersion(this, new RepositoryPath(path), version);
	}
	
	public DatabaseRepositoryDao getRepositoryDao() {
		return this.databaseRepositoryDao;
	}

}
