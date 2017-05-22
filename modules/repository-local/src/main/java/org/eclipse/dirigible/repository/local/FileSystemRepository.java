/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

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
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.zip.ZipExporter;
import org.eclipse.dirigible.repository.zip.ZipImporter;

/**
 * The File System based implementation of {@link IRepository}
 */
public class FileSystemRepository implements IRepository {
	
	private static Logger logger = Logger.getLogger(FileSystemRepository.class);

	private static final String CURRENT_DIR = ICommonConstants.DOT;
	private static final String DIRIGIBLE_LOCAL = "dirigible_local";
	public static final String PATH_SEGMENT_ROOT = "root";
	public static final String PATH_SEGMENT_VERSIONS = "versions";
	public static final String PATH_SEGMENT_INFO = "info";

	private static final String PROVIDED_ZIP_DATA_CANNOT_BE_NULL = "Provided Zip Data cannot be null"; //$NON-NLS-1$
	private static final String PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL = "Provided Zip Input Stream cannot be null"; //$NON-NLS-1$

	public static final String PATH_DELIMITER = IRepository.SEPARATOR;
	private String repositoryPath = IRepository.SEPARATOR;
	private String versionsPath = IRepository.SEPARATOR;
	private String infoPath = IRepository.SEPARATOR;

	private LocalRepositoryDAO repositoryDAO;

	private String user;

	protected FileSystemRepository() throws LocalBaseException {
	}

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param user
	 * @throws LocalBaseException
	 */
	public FileSystemRepository(String user) throws LocalBaseException {
		createRepository(user, null, false);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user
	 * @param rootFolder
	 * @throws LocalBaseException
	 */
	public FileSystemRepository(String user, String rootFolder) throws LocalBaseException {
		createRepository(user, rootFolder, false);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param user
	 * @param rootFolder
	 * @param absolute
	 * @throws LocalBaseException
	 */
	public FileSystemRepository(String user, String rootFolder, boolean absolute) throws LocalBaseException {
		createRepository(user, rootFolder, absolute);
	}

	protected void createRepository(String user, String rootFolder, boolean absolute) {
		String root;
		if (absolute) {
			if (rootFolder != null) {
				root = rootFolder;
			} else {
				throw new LocalBaseException("Creating a FileSystemRepository with absolute path flag, but the path itself is null");
			}
		} else {
			root = System.getProperty("user.dir");
			if ((rootFolder != null) && !rootFolder.equals(CURRENT_DIR)) {
				root += File.separator;
				root += rootFolder;
			}
		}
		this.user = user;
		this.repositoryDAO = new LocalRepositoryDAO(this);

		logger.debug(String.format("Creating File-based Repository Client for: %s ...", root));
		initializeRepository(root);
		logger.debug(String.format("File-based Repository Client for: %s, has been created.", root));
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public String getVersionsPath() {
		return versionsPath;
	}

	public String getInfoPath() {
		return infoPath;
	}

	protected String getRepositoryRootFolder() {
		return DIRIGIBLE_LOCAL;
	}

	private void initializeRepository(String rootFolder) {
		repositoryPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_ROOT; // $NON-NLS-1$
		repositoryPath = repositoryPath.replace(IRepository.SEPARATOR, File.separator);
		versionsPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_VERSIONS; // $NON-NLS-1$
		versionsPath = versionsPath.replace(IRepository.SEPARATOR, File.separator);
		infoPath = rootFolder + IRepository.SEPARATOR + getRepositoryRootFolder() + IRepository.SEPARATOR + PATH_SEGMENT_INFO; // $NON-NLS-1$
		infoPath = infoPath.replace(IRepository.SEPARATOR, File.separator);
	}

	@Override
	public ICollection getRoot() {
		logger.debug("entering getRoot"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(IRepository.SEPARATOR);
		LocalCollection dbCollection = new LocalCollection(this, wrapperPath);
		logger.debug("exiting getRoot"); //$NON-NLS-1$
		return dbCollection;
	}

	@Override
	public ICollection createCollection(String path) throws IOException {
		logger.debug("entering createCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final LocalCollection collection = new LocalCollection(this, wrapperPath);
		collection.create();
		logger.debug("exiting createCollection"); //$NON-NLS-1$
		return collection;
	}

	@Override
	public ICollection getCollection(String path) {
		logger.debug("entering getCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		LocalCollection localCollection = new LocalCollection(this, wrapperPath);
		logger.debug("exiting getCollection"); //$NON-NLS-1$
		return localCollection;
	}

	@Override
	public void removeCollection(String path) throws IOException {
		logger.debug("entering removeCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection collection = new LocalCollection(this, wrapperPath);
		collection.delete();
		logger.debug("exiting removeCollection"); //$NON-NLS-1$
	}

	@Override
	public boolean hasCollection(String path) throws IOException {
		logger.debug("entering hasCollection"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final ICollection collection = new LocalCollection(this, wrapperPath);
		boolean result = collection.exists();
		logger.debug("exiting hasCollection"); //$NON-NLS-1$
		return result;
	}

	@Override
	public IResource createResource(String path) throws IOException {
		logger.debug("entering createResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.create();
		logger.debug("exiting createResource"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public IResource createResource(String path, byte[] content) throws IOException {
		logger.debug("entering createResource with Content"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.setContent(content);
		logger.debug("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType) throws IOException {
		return createResource(path, content, isBinary, contentType, false);
	}

	@Override
	public IResource createResource(String path, byte[] content, boolean isBinary, String contentType, boolean override) throws IOException {
		logger.debug("entering createResource with Content"); //$NON-NLS-1$
		try {
			getRepositoryDAO().createFile(path, content, isBinary, contentType);
		} catch (LocalBaseException e) {
			throw new IOException(e);
		}
		final IResource resource = getResource(path);
		logger.debug("exiting createResource with Content"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public IResource getResource(String path) {
		logger.debug("entering getResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		LocalResource resource = new LocalResource(this, wrapperPath);
		logger.debug("exiting getResource"); //$NON-NLS-1$
		return resource;
	}

	@Override
	public void removeResource(String path) throws IOException {
		logger.debug("entering removeResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		resource.delete();
		logger.debug("exiting removeResource"); //$NON-NLS-1$
	}

	@Override
	public boolean hasResource(String path) throws IOException {
		logger.debug("entering hasResource"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(path);
		final IResource resource = new LocalResource(this, wrapperPath);
		boolean result = resource.exists();
		logger.debug("exiting hasResource"); //$NON-NLS-1$
		return result;
	}

	@Override
	public void dispose() {
		// repositoryDAO.dispose();
	}

	public LocalRepositoryDAO getRepositoryDAO() {
		return repositoryDAO;
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String path) throws IOException {
		importZip(zipInputStream, path, false);
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String path, boolean override) throws IOException {
		importZip(zipInputStream, path, override, false);
	}

	@Override
	public void importZip(ZipInputStream zipInputStream, String relativeRoot, boolean override, boolean excludeRootFolderName) throws IOException {
		if (zipInputStream == null) {
			logger.error(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
			throw new IOException(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
		}
		// TODO make use of override and excludeRootFolderName arguments?
		// importer.unzip(relativeRoot, zipInputStream, null);
		ZipImporter.importZip(this, zipInputStream, relativeRoot, override, excludeRootFolderName);
	}

	@Override
	public void importZip(byte[] data, String path) throws IOException {
		importZip(data, path, false);
	}

	@Override
	public void importZip(byte[] data, String path, boolean override) throws IOException {
		importZip(data, path, override, false, null);
	}

	@Override
	public void importZip(byte[] data, String relativeRoot, boolean override, boolean excludeRootFolderName, Map<String, String> filter)
			throws IOException {
		if (data == null) {
			logger.error(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
			throw new IOException(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
		}
		// TODO make use of override and excludeRootFolderName arguments?
		// importer.unzip(relativeRoot, new ZipInputStream(new ByteArrayInputStream(data)), filter);
		ZipImporter.importZip(this, new ZipInputStream(new ByteArrayInputStream(data)), relativeRoot, override, excludeRootFolderName, filter);
	}

	// @Override
	// public byte[] exportZip(List<String> relativeRoots) throws IOException {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
	// exporter.zip(relativeRoots, zipOutputStream);
	// return baos.toByteArray();
	// }
	//
	// @Override
	// public byte[] exportZip(String path, boolean inclusive) throws IOException {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
	// try {
	// exporter.zip(path, zipOutputStream, inclusive);
	// } finally {
	// zipOutputStream.flush();
	// zipOutputStream.close();
	// }
	//
	// return baos.toByteArray();
	// }

	@Override
	public byte[] exportZip(List<String> relativeRoots) throws IOException {
		return ZipExporter.exportZip(this, relativeRoots);
	}

	@Override
	public byte[] exportZip(String relativeRoot, boolean inclusive) throws IOException {
		return ZipExporter.exportZip(this, relativeRoot, inclusive);
	}

	@Override
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws IOException {
		// return repositoryDAO.searchName(parameter, caseInsensitive);
		return null;
	}

	@Override
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) throws IOException {

		String workspacePath = LocalWorkspaceMapper.getMappedName(this, root);

		List<IEntity> entities = new ArrayList<IEntity>();

		if ((parameter == null) || "".equals(parameter)) {
			return entities;
		}

		File dir = new File(workspacePath);

		Iterator<File> foundFiles = FileUtils.iterateFiles(dir,
				new WildcardFileFilter("*" + parameter + "*", (caseInsensitive ? INSENSITIVE : SENSITIVE)), TRUE);
		while (foundFiles.hasNext()) {
			File foundFile = foundFiles.next();
			String repositoryName = foundFile.getCanonicalPath().substring(getRepositoryPath().length());
			RepositoryPath localRepositoryPath = new RepositoryPath(repositoryName);
			entities.add(new LocalResource(this, localRepositoryPath));
		}

		return entities;
	}

	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws IOException {
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
	}

	@Override
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) throws IOException {
		// return repositoryDAO.searchText(parameter, caseInsensitive);
		return null;
	}

	@Override
	public List<IResourceVersion> getResourceVersions(String path) throws IOException {
		return repositoryDAO.getResourceVersionsByPath(path);
	}

	@Override
	public IResourceVersion getResourceVersion(String path, int version) throws IOException {
		List<IResourceVersion> allVersions = getResourceVersions(path);
		for (IResourceVersion resourceVersion : allVersions) {
			if (resourceVersion.getVersion() == version) {
				return resourceVersion;
			}
		}
		return null;
	}

	@Override
	public void cleanupOldVersions() throws IOException {
		String versionsRoot = getVersionsPath();
		synchronized (this.getClass()) {
			GregorianCalendar last = new GregorianCalendar();
			last.add(Calendar.WEEK_OF_YEAR, -1);
			thresholdDate = last.getTime();
			cleanOlderFiles(new File(versionsRoot));
		}
	}

	Date thresholdDate;

	private void cleanOlderFiles(File file) {
		Iterator<File> filesToBeDeleted = FileUtils.iterateFiles(file, new AgeFileFilter(thresholdDate), TRUE);
		while (filesToBeDeleted.hasNext()) {
			File toBeDeleted = filesToBeDeleted.next();
			toBeDeleted.delete();
		}
	}

	@Override
	public String getUser() {
		return user;
	}

}
