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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * The File System based implementation of {@link IRepository}
 */
public class FileSystemRepository implements IRepository {

	private static final String PROVIDED_ZIP_DATA_CANNOT_BE_NULL = Messages.getString("DBRepository.PROVIDED_ZIP_DATA_CANNOT_BE_NULL"); //$NON-NLS-1$

	private static final String PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL = Messages
			.getString("DBRepository.PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL"); //$NON-NLS-1$

	private static Logger logger = Logger.getLogger(FileSystemRepository.class);

	public static final String PATH_DELIMITER = IRepository.SEPARATOR;

	private String repositoryPath = IRepository.SEPARATOR;
	private String versionsPath = IRepository.SEPARATOR;
	private String infoPath = IRepository.SEPARATOR;

	private LocalRepositoryDAO repositoryDAO;
	private LocalZipImporter importer;
	private LocalZipExporter exporter;

	private String user;

	/**
	 * Constructor with default root folder - user.dir and without database initialization
	 *
	 * @param user
	 * @throws LocalBaseException
	 */
	public FileSystemRepository(String user) throws LocalBaseException {
		this(user, null);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param dataSource
	 * @param user
	 * @param rootFolder
	 * @throws LocalBaseException
	 */
	public FileSystemRepository(String user, String rootFolder) throws LocalBaseException {

		String root = rootFolder;
		if (root == null) {
			root = System.getProperty("user.dir");
		}

		this.user = user;
		this.repositoryDAO = new LocalRepositoryDAO(this);
		this.importer = new LocalZipImporter(this);
		this.exporter = new LocalZipExporter(this);

		initializeRepository(root);
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

	private void initializeRepository(String rootFolder) {
		repositoryPath = rootFolder + "/dirigible_local/root"; // $NON-NLS-1$
		repositoryPath = repositoryPath.replace(IRepository.SEPARATOR, File.separator);
		versionsPath = rootFolder + "/dirigible_local/versions"; // $NON-NLS-1$
		versionsPath = versionsPath.replace(IRepository.SEPARATOR, File.separator);
		infoPath = rootFolder + "/dirigible_local/info"; // $NON-NLS-1$
		infoPath = infoPath.replace(IRepository.SEPARATOR, File.separator);
	}

	@Override
	public ICollection getRoot() {
		logger.debug("entering getRoot"); //$NON-NLS-1$
		final RepositoryPath wrapperPath = new RepositoryPath(repositoryPath);
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
	public void importZip(ZipInputStream zipInputStream, String path, boolean override, boolean excludeRootFolderName) throws IOException {
		if (zipInputStream == null) {
			logger.error(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
			throw new IOException(PROVIDED_ZIP_INPUT_STREAM_CANNOT_BE_NULL);
		}
		// TODO make use of override and excludeRootFolderName arguments?
		importer.unzip(path, zipInputStream, null);

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
	public void importZip(byte[] data, String path, boolean override, boolean excludeRootFolderName, Map<String, String> filter) throws IOException {
		if (data == null) {
			logger.error(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
			throw new IOException(PROVIDED_ZIP_DATA_CANNOT_BE_NULL);
		}
		// TODO make use of override and excludeRootFolderName arguments?
		importer.unzip(path, new ZipInputStream(new ByteArrayInputStream(data)), filter);

	}

	@Override
	public byte[] exportZip(List<String> relativeRoots) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
		exporter.zip(relativeRoots, zipOutputStream);
		return baos.toByteArray();
	}

	@Override
	public byte[] exportZip(String path, boolean inclusive) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
		try {
			exporter.zip(path, zipOutputStream, inclusive);
		} finally {
			zipOutputStream.flush();
			zipOutputStream.close();
		}

		return baos.toByteArray();
	}

	// @Override
	// public byte[] exportZip(List<String> relativeRoots) throws IOException {
	// return ZipExporter.exportZip(this, relativeRoots);
	// }
	//
	// @Override
	// public byte[] exportZip(String relativeRoot, boolean inclusive) throws IOException {
	// return ZipExporter.exportZip(this, relativeRoot, inclusive);
	// }

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

		if (parameter.startsWith("%")) {
			parameter = parameter.substring(1);
		}

		File dir = new File(workspacePath);
		findInDirectory(dir, parameter, entities);

		return entities;
	}

	private void findInDirectory(File dir, String parameter, List<IEntity> entities) throws IOException {

		final String search = parameter;
		File[] found = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(search);
			}
		});

		for (File f : found) {
			String repositoryName = f.getCanonicalPath().substring(getRepositoryPath().length());
			RepositoryPath repositoryPath = new RepositoryPath(repositoryName);
			entities.add(new LocalResource(this, repositoryPath));
		}

		File[] all = dir.listFiles();
		for (File f : all) {
			if (f.isDirectory()) {
				findInDirectory(f, parameter, entities);
			}
		}
	}

	@Override
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) throws IOException {
		// return repositoryDAO.searchPath(parameter, caseInsensitive);
		return null;
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
		// return new DBResourceVersion(this, new RepositoryPath(path), version);
		return null;
	}

	@Override
	public void cleanupOldVersions() throws IOException {
		// repositoryDAO.cleanupOldVersions();
	}

	@Override
	public String getUser() {
		return user;
	}

}
