/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.fs;

import static org.apache.commons.io.IOCase.INSENSITIVE;
import static org.apache.commons.io.IOCase.SENSITIVE;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.local.LocalCollection;
import org.eclipse.dirigible.repository.local.LocalRepositoryDao;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.local.LocalResource;
import org.eclipse.dirigible.repository.local.LocalWorkspaceMapper;
import org.eclipse.dirigible.repository.zip.RepositoryZipExporter;
import org.eclipse.dirigible.repository.zip.RepositoryZipImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based implementation of {@link IRepository}.
 */
public abstract class FileSystemRepository implements IRepository {

	private static Logger logger = LoggerFactory.getLogger(FileSystemRepository.class);

	private static final String CURRENT_DIR = ".";

	private static final String DIRIGIBLE_LOCAL = "dirigible" + IRepository.SEPARATOR + "repository";

	private static final String PATH_SEGMENT_ROOT = "root";

	private static final String PATH_SEGMENT_VERSIONS = "versions";

	private static final String PATH_SEGMENT_INFO = "info";

	private String repositoryPath = IRepository.SEPARATOR;

	private String versionsPath = IRepository.SEPARATOR;

	private String infoPath = IRepository.SEPARATOR;

	private LocalRepositoryDao repositoryDao;

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public FileSystemRepository() throws LocalRepositoryException {
		createRepository(null, false);
	}

	/**
	 * Constructor with root folder parameter.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public FileSystemRepository(String rootFolder) throws LocalRepositoryException {
		createRepository(rootFolder, false);
	}

	/**
	 * Constructor with root folder parameter.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @param absolute
	 *            whether the root folder is absolute
	 * @throws LocalRepositoryException
	 *             in case the repository cannot be created
	 */
	public FileSystemRepository(String rootFolder, boolean absolute) throws LocalRepositoryException {
		createRepository(rootFolder, absolute);
	}

	/**
	 * Creates the repository.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @param absolute
	 *            the absolute
	 */
	protected void createRepository(String rootFolder, boolean absolute) {
		String root;
		if (absolute) {
			if (rootFolder != null) {
				root = rootFolder;
			} else {
				throw new LocalRepositoryException("Creating a FileSystemRepository with absolute path flag, but the path itself is null");
			}
		} else {
			root = System.getProperty("user.dir");
			if ((rootFolder != null) && !rootFolder.equals(CURRENT_DIR)) {
				root += File.separator;
				root += rootFolder;
			}
		}
		this.repositoryDao = new LocalRepositoryDao(this);

		logger.debug(String.format("Creating File-based Repository Client for: %s ...", root));
		try {
			initializeRepository(root);
		} catch (IOException e) {
			throw new LocalRepositoryException();
		}
		logger.debug(String.format("File-based Repository Client for: %s, has been created.", root));
	}

	/**
	 * Gets the repository path.
	 *
	 * @return the repository path
	 */
	public String getRepositoryPath() {
		return repositoryPath;
	}

	/**
	 * Gets the versions path.
	 *
	 * @return the versions path
	 */
	public String getVersionsPath() {
		return versionsPath;
	}

	/**
	 * Gets the info path.
	 *
	 * @return the info path
	 */
	public String getInfoPath() {
		return infoPath;
	}

	/**
	 * Gets the repository root folder.
	 *
	 * @return the repository root folder
	 */
	protected String getRepositoryRootFolder() {
		return DIRIGIBLE_LOCAL;
	}

	/**
	 * Initialize repository.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void initializeRepository(String rootFolder) throws IOException {
		repositoryPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_ROOT; // $NON-NLS-1$
		repositoryPath = repositoryPath.replace(IRepository.SEPARATOR, File.separator);
		repositoryPath = new File(repositoryPath).getCanonicalPath();
		versionsPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_VERSIONS; // $NON-NLS-1$
		versionsPath = versionsPath.replace(IRepository.SEPARATOR, File.separator);
		versionsPath = new File(versionsPath).getCanonicalPath();
		infoPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_INFO; // $NON-NLS-1$
		infoPath = infoPath.replace(IRepository.SEPARATOR, File.separator);
		infoPath = new File(infoPath).getCanonicalPath();
		FileSystemUtils.createFolder(repositoryPath);
		FileSystemUtils.createFolder(versionsPath);
		FileSystemUtils.createFolder(infoPath);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#getRoot()
	 */
	@Override
	public ICollection getRoot() {
		logger.trace("entering getRoot"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(IRepository.SEPARATOR);
		LocalCollection localCollection = new LocalCollection(this, wrapperPath);
		logger.trace("exiting getRoot"); //$NON-NLS-1$
		return localCollection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#createCollection(java.lang.String)
	 */
	@Override
	public ICollection createCollection(String path) throws RepositoryWriteException {
		logger.trace("entering createCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final LocalCollection localCollection = new LocalCollection(this, wrapperPath);
		localCollection.create();
		logger.trace("exiting createCollection"); //$NON-NLS-1$
		return localCollection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#getCollection(java.lang.String)
	 */
	@Override
	public ICollection getCollection(String path) {
		logger.trace("entering getCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		LocalCollection localCollection = new LocalCollection(this, wrapperPath);
		logger.trace("exiting getCollection"); //$NON-NLS-1$
		return localCollection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#removeCollection(java.lang.String)
	 */
	@Override
	public void removeCollection(String path) throws RepositoryWriteException {
		logger.trace("entering removeCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection collection = new LocalCollection(this, wrapperPath);
		collection.delete();
		logger.trace("exiting removeCollection"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#hasCollection(java.lang.String)
	 */
	@Override
	public boolean hasCollection(String path) throws RepositoryReadException {
		logger.trace("entering hasCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection collection = new LocalCollection(this, wrapperPath);
		boolean result = collection.exists();
		logger.trace("exiting hasCollection"); //$NON-NLS-1$
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#createResource(java.lang.String)
	 */
	@Override
	public IResource createResource(String path) throws RepositoryWriteException {
		logger.trace("entering createResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.create();
		logger.trace("exiting createResource"); //$NON-NLS-1$
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#createResource(java.lang.String, byte[])
	 */
	@Override
	public IResource createResource(String path, byte[] content) throws RepositoryWriteException {
		logger.trace("entering createResource with Content"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.setContent(content);
		logger.trace("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#createResource(java.lang.String, byte[], boolean,
	 * java.lang.String)
	 */
	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return createResource(path, content, isBinary, contentType, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#createResource(java.lang.String, byte[], boolean,
	 * java.lang.String, boolean)
	 */
	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override)
			throws RepositoryWriteException {
		logger.trace("entering createResource with Content"); //$NON-NLS-1$
		try {
			final RepositoryPath wrapperPath = new RepositoryPath(path);
			getRepositoryDao().createFile(wrapperPath.toString(), content, isBinary, contentType);
		} catch (LocalRepositoryException e) {
			throw new RepositoryWriteException(e);
		}
		final IResource resource = getResource(path);
		logger.trace("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#getResource(java.lang.String)
	 */
	@Override
	public IResource getResource(String path) {
		logger.trace("entering getResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		LocalResource resource = new LocalResource(this, wrapperPath);
		logger.trace("exiting getResource"); //$NON-NLS-1$
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String path) throws RepositoryWriteException {
		logger.trace("entering removeResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.delete();
		logger.trace("exiting removeResource"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryReader#hasResource(java.lang.String)
	 */
	@Override
	public boolean hasResource(String path) throws RepositoryReadException {
		logger.trace("entering hasResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		boolean result = resource.exists();
		logger.trace("exiting hasResource"); //$NON-NLS-1$
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#dispose()
	 */
	@Override
	public void dispose() {
		// repositoryDAO.dispose();
	}

	/**
	 * Gets the repository dao.
	 *
	 * @return the repository dao
	 */
	public LocalRepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(java.util.zip.ZipInputStream,
	 * java.lang.String)
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String path) throws RepositoryImportException {
		importZip(zipInputStream, path, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(java.util.zip.ZipInputStream,
	 * java.lang.String, boolean)
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String path, boolean override) throws RepositoryImportException {
		importZip(zipInputStream, path, override, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(java.util.zip.ZipInputStream,
	 * java.lang.String, boolean, boolean)
	 */
	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName)
			throws RepositoryImportException {
		if (zipInputStream == null) {
			logger.error("Provided Zip Input Stream cannot be null");
			throw new RepositoryImportException("Provided Zip Input Stream cannot be null");
		}
		RepositoryZipImporter.importZip(this, zipInputStream, relativeRoot, override, excludeRootFolderName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(byte[], java.lang.String)
	 */
	@Override
	public void importZip(byte[] data, String path) throws RepositoryImportException {
		importZip(data, path, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(byte[], java.lang.String, boolean)
	 */
	@Override
	public void importZip(byte[] data, String path, boolean override) throws RepositoryImportException {
		importZip(data, path, override, false, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryImporter#importZip(byte[], java.lang.String, boolean,
	 * boolean, java.util.Map)
	 */
	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws RepositoryImportException {
		if (data == null) {
			logger.error("Provided Zip Data cannot be null");
			throw new RepositoryImportException("Provided Zip Data cannot be null");
		}
		RepositoryZipImporter.importZip(this, new ZipInputStream(new ByteArrayInputStream(data)), relativeRoot, override, excludeRootFolderName,
				filter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryExporter#exportZip(java.util.List)
	 */
	@Override
	public byte[] exportZip(List<String> relativeRoots) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoots);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryExporter#exportZip(java.lang.String, boolean)
	 */
	@Override
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws RepositoryExportException {
		return RepositoryZipExporter.exportZip(this, relativeRoot, inclusive);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositorySearch#searchName(java.lang.String, boolean)
	 */
	@Override
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		// return repositoryDAO.searchName(parameter, caseInsensitive);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositorySearch#searchName(java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws RepositorySearchException {

		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(this, root);

			List<IEntity> entities = new ArrayList<IEntity>();

			if ((parameter == null) || "".equals(parameter)) {
				return entities;
			}

			String rootRepositoryPath = getRepositoryPath();
			File dir = new File(workspacePath);
			Iterator<File> foundFiles = FileUtils.iterateFiles(dir,
					new WildcardFileFilter("*" + parameter + "*", (caseInsensitive ? INSENSITIVE : SENSITIVE)), TRUE);
			while (foundFiles.hasNext()) {
				File foundFile = foundFiles.next();
				if (foundFile.getCanonicalPath().length() <= rootRepositoryPath.length()) {
					throw new RepositorySearchException(String.format("The found file name [%s] is shorter than the repository root file name [%s]",
							foundFile.getCanonicalPath(), rootRepositoryPath));
				}
				String repositoryName = foundFile.getCanonicalPath().substring(rootRepositoryPath.length());
				RepositoryPath localRepositoryPath = new RepositoryPath(repositoryName);
				entities.add(new LocalResource(this, localRepositoryPath));
			}

			return entities;
		} catch (RepositoryWriteException | IOException e) {
			throw new RepositorySearchException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositorySearch#searchPath(java.lang.String, boolean)
	 */
	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		try {
			String rootRepositoryPath = getRepositoryPath();
			List<IEntity> entities = new ArrayList<IEntity>();
			Iterator<File> foundFiles = FileUtils.iterateFiles(new File(rootRepositoryPath),
					new WildcardFileFilter("*" + parameter + "*", (caseInsensitive ? INSENSITIVE : SENSITIVE)), TRUE);
			while (foundFiles.hasNext()) {
				File foundFile = foundFiles.next();
				String repositoryName = foundFile.getCanonicalPath().substring(getRepositoryPath().length());
				RepositoryPath localRepositoryPath = new RepositoryPath(repositoryName);
				entities.add(new LocalResource(this, localRepositoryPath));
			}

			return entities;
		} catch (IOException e) {
			throw new RepositorySearchException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositorySearch#searchText(java.lang.String, boolean)
	 */
	@Override
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		// return repositoryDAO.searchText(parameter, caseInsensitive);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryVersioning#getResourceVersions(java.lang.String)
	 */
	@Override
	public List<IResourceVersion> getResourceVersions(String path) throws RepositorySearchException {
		return repositoryDao.getResourceVersionsByPath(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryVersioning#getResourceVersion(java.lang.String, int)
	 */
	@Override
	public IResourceVersion getResourceVersion(String path, int version) throws RepositorySearchException {
		List<IResourceVersion> allVersions = getResourceVersions(path);
		for (IResourceVersion resourceVersion : allVersions) {
			if (resourceVersion.getVersion() == version) {
				return resourceVersion;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IRepositoryWriter#cleanupOldVersions()
	 */
	@Override
	public void cleanupOldVersions() throws RepositoryWriteException {
		String versionsRoot = getVersionsPath();
		synchronized (this.getClass()) {
			GregorianCalendar last = new GregorianCalendar();
			last.add(Calendar.WEEK_OF_YEAR, -1);
			thresholdDate = last.getTime();
			try {
				cleanOlderFiles(new File(versionsRoot));
			} catch (IOException e) {
				throw new RepositoryWriteException(e);
			}
		}
	}

	/** The threshold date. */
	Date thresholdDate;

	/**
	 * Clean older files.
	 *
	 * @param file
	 *            the file
	 * @throws IOException
	 */
	private void cleanOlderFiles(File file) throws IOException {
		Iterator<File> filesToBeDeleted = FileUtils.iterateFiles(file, new AgeFileFilter(thresholdDate), TRUE);
		while (filesToBeDeleted.hasNext()) {
			File toBeDeleted = filesToBeDeleted.next();
			boolean deleted = toBeDeleted.delete();
			if (!deleted) {
				logger.error("Error on deleting the file: " + toBeDeleted.getCanonicalPath());
			}
		}
	}

}
