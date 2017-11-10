/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.fs.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalRepositoryDao.
 */
public class LocalRepositoryDao {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(LocalRepositoryDao.class);

	/** The Constant LAST. */
	private static final String LAST = "last";

	/** The Constant MODIFIED_AT. */
	private static final String MODIFIED_AT = "modifiedAt";

	/** The Constant MODIFIED_BY. */
	private static final String MODIFIED_BY = "modifiedBy";

	/** The Constant CREATED_AT. */
	private static final String CREATED_AT = "createdAt";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "createdBy";

	/** The Constant OBJECT_TYPE_FOLDER. */
	static final int OBJECT_TYPE_FOLDER = 0;
	
	/** The Constant OBJECT_TYPE_DOCUMENT. */
	static final int OBJECT_TYPE_DOCUMENT = 1;
	
	/** The Constant OBJECT_TYPE_BINARY. */
	static final int OBJECT_TYPE_BINARY = 2;

	/** The repository. */
	private FileSystemRepository repository;

	/**
	 * Instantiates a new local repository dao.
	 *
	 * @param repository the repository
	 */
	public LocalRepositoryDao(FileSystemRepository repository) {
		this.repository = repository;
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public FileSystemRepository getRepository() {
		return this.repository;
	}

	/**
	 * Creates the file.
	 *
	 * @param path the path
	 * @param content the content
	 * @param isBinary the is binary
	 * @param contentType the content type
	 * @throws LocalRepositoryException the local repository exception
	 */
	public void createFile(String path, byte[] content, boolean isBinary, String contentType) throws LocalRepositoryException {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			FileSystemUtils.saveFile(workspacePath, content);
			createVersion(workspacePath, content);
			createInfo(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}

	}

	/**
	 * Creates the version.
	 *
	 * @param workspacePath the workspace path
	 * @param content the content
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createVersion(String workspacePath, byte[] content) throws FileNotFoundException, IOException {
		String versionsPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getVersionsPath());
		if (FileSystemUtils.directoryExists(versionsPath)) {
			String versionsLastPath = versionsPath + File.separator + LAST;
			byte[] bytes = FileSystemUtils.loadFile(versionsLastPath);
			if (bytes != null) {
				Integer index;
				try {
					index = Integer.parseInt(new String(bytes, IRepository.UTF8));
					FileSystemUtils.saveFile(versionsPath + File.separator + (++index), content);
					FileSystemUtils.saveFile(versionsLastPath, index.toString().getBytes(IRepository.UTF8));
				} catch (NumberFormatException e) {
					logger.error(String.format("Invalid versions file: %s", versionsLastPath));
					createInitialVersion(content, versionsPath);
				}
			}
		} else {
			createInitialVersion(content, versionsPath);
		}
	}

	/**
	 * Creates the initial version.
	 *
	 * @param content the content
	 * @param versionsPath the versions path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createInitialVersion(byte[] content, String versionsPath) throws FileNotFoundException, IOException {
		FileSystemUtils.saveFile(versionsPath + File.separator + "1", content);
		FileSystemUtils.saveFile(versionsPath + File.separator + LAST, "1".getBytes(IRepository.UTF8));
	}

	/**
	 * Creates the info.
	 *
	 * @param workspacePath the workspace path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createInfo(String workspacePath) throws FileNotFoundException, IOException {
		String infoPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getInfoPath());
		if (FileSystemUtils.fileExists(infoPath)) {
			byte[] bytes = FileSystemUtils.loadFile(infoPath);
			if (bytes != null) {
				Properties info = new Properties();
				info.load(new ByteArrayInputStream(bytes));
				info.setProperty(MODIFIED_BY, getUser());
				info.setProperty(MODIFIED_AT, new Date().getTime() + "");

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				info.store(out, "");
				FileSystemUtils.saveFile(infoPath, out.toByteArray());
			}
		} else {
			createInitialInfo(infoPath);
		}

	}

	/**
	 * Creates the initial info.
	 *
	 * @param infoPath the info path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createInitialInfo(String infoPath) throws IOException {
		Properties info = new Properties();
		info.setProperty(CREATED_BY, getUser());
		info.setProperty(CREATED_AT, new Date().getTime() + "");
		info.setProperty(MODIFIED_BY, getUser());
		info.setProperty(MODIFIED_AT, new Date().getTime() + "");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		info.store(out, "");
		FileSystemUtils.saveFile(infoPath, out.toByteArray());
	}

	/**
	 * Removes the versions.
	 *
	 * @param workspacePath the workspace path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void removeVersions(String workspacePath) throws FileNotFoundException, IOException {
		String versionsPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getVersionsPath());
		if (FileSystemUtils.directoryExists(versionsPath)) {
			FileSystemUtils.removeFile(versionsPath);
		}
	}

	/**
	 * Removes the info.
	 *
	 * @param workspacePath the workspace path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void removeInfo(String workspacePath) throws FileNotFoundException, IOException {
		String infoPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getInfoPath());
		if (FileSystemUtils.fileExists(infoPath)) {
			FileSystemUtils.removeFile(infoPath);
		}
	}

	/**
	 * Check initialized.
	 */
	public void checkInitialized() {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the file content.
	 *
	 * @param localFile the local file
	 * @param content the content
	 */
	public void setFileContent(LocalFile localFile, byte[] content) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), localFile.getPath());
			FileSystemUtils.saveFile(workspacePath, content);
			createVersion(workspacePath, content);
			createInfo(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Gets the file content.
	 *
	 * @param localFile the local file
	 * @return the file content
	 */
	public byte[] getFileContent(LocalFile localFile) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), localFile.getPath());
			return FileSystemUtils.loadFile(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Rename file.
	 *
	 * @param path the path
	 * @param newPath the new path
	 */
	public void renameFile(String path, String newPath) {
		try {
			String workspacePathOld = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			String workspacePathNew = LocalWorkspaceMapper.getMappedName(getRepository(), newPath);
			FileSystemUtils.moveFile(workspacePathOld, workspacePathNew);
			byte[] content = FileSystemUtils.loadFile(workspacePathNew);
			if (content != null) {
				createVersion(workspacePathNew, content);
				createInfo(workspacePathNew);
				removeVersions(workspacePathOld);
				removeInfo(workspacePathOld);
			}
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Copy file.
	 *
	 * @param path the path
	 * @param newPath the new path
	 */
	public void copyFile(String path, String newPath) {
		try {
			String workspacePathOld = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			String workspacePathNew = LocalWorkspaceMapper.getMappedName(getRepository(), newPath);
			FileSystemUtils.copyFile(workspacePathOld, workspacePathNew);
			byte[] content = FileSystemUtils.loadFile(workspacePathNew);
			if (content != null) {
				createVersion(workspacePathNew, content);
				createInfo(workspacePathNew);
			}
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Removes the file by path.
	 *
	 * @param path the path
	 */
	public void removeFileByPath(String path) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			FileSystemUtils.removeFile(workspacePath);
			removeVersions(workspacePath);
			removeInfo(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Removes the folder by path.
	 *
	 * @param path the path
	 */
	public void removeFolderByPath(String path) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			FileSystemUtils.removeFile(workspacePath);
			removeVersions(workspacePath);
			removeInfo(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Creates the folder.
	 *
	 * @param normalizePath the normalize path
	 */
	public void createFolder(String normalizePath) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), normalizePath);
			FileSystemUtils.createFolder(workspacePath);
		} catch (RepositoryWriteException e) {
			throw new LocalRepositoryException(e);
		}
	}

	/**
	 * Rename folder.
	 *
	 * @param path the path
	 * @param newPath the new path
	 */
	public void renameFolder(String path, String newPath) {
		try {
			String workspacePathOld = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			String workspacePathNew = LocalWorkspaceMapper.getMappedName(getRepository(), newPath);
			FileSystemUtils.moveFile(workspacePathOld, workspacePathNew);
			removeVersions(workspacePathNew);
			removeInfo(workspacePathNew);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}

	}

	/**
	 * Copy folder.
	 *
	 * @param path the path
	 * @param newPath the new path
	 */
	public void copyFolder(String path, String newPath) {
		try {
			String workspacePathOld = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			String workspacePathNew = LocalWorkspaceMapper.getMappedName(getRepository(), newPath);
			FileSystemUtils.copyFolder(workspacePathOld, workspacePathNew);
			createInfo(workspacePathNew);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}

	}

	/**
	 * Gets the object by path.
	 *
	 * @param path the path
	 * @return the object by path
	 */
	public LocalObject getObjectByPath(String path) {

		LocalObject localObject = null;

		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			File objectFile = new File(workspacePath);
			if (!objectFile.exists()) {
				// This is folder, that was not created
				if (ContentTypeHelper.getExtension(workspacePath).isEmpty() && !workspacePath.endsWith(".")) {
					// FileSystemUtils.createFolder(workspacePath);
					return null;
				}
			}
			if (objectFile.isFile()) {
				String contentType = ContentTypeHelper.getContentType(FileSystemUtils.getExtension(workspacePath));
				localObject = new LocalFile(repository, ContentTypeHelper.isBinary(contentType), contentType);
			} else {
				localObject = new LocalFolder(repository);
			}
			localObject.setName(objectFile.getName());
			localObject.setPath(workspacePath);

			String infoPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getInfoPath());
			if (FileSystemUtils.fileExists(infoPath)) {
				try {
					byte[] bytes = FileSystemUtils.loadFile(infoPath);
					if (bytes != null) {
						Properties info = new Properties();
						info.load(new ByteArrayInputStream(bytes));
						localObject.setCreatedBy(info.getProperty(CREATED_BY));
						String prop = info.getProperty(CREATED_AT);
						if (prop != null) {
							localObject.setCreatedAt(new Date(Long.parseLong(prop)));
						}
						localObject.setModifiedBy(info.getProperty(MODIFIED_BY));
						prop = info.getProperty(MODIFIED_AT);
						if (prop != null) {
							localObject.setModifiedAt(new Date(Long.parseLong(prop)));
						}
					}
				} catch (Throwable e) {
					throw new LocalRepositoryException(e);
				}
			}

		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
		return localObject;

	}

	/**
	 * Gets the children by folder.
	 *
	 * @param path the path
	 * @return the children by folder
	 */
	public List<LocalObject> getChildrenByFolder(String path) {
		List<LocalObject> localObjects = new ArrayList<LocalObject>();
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			File objectFile = new File(workspacePath);
			if (objectFile.isDirectory()) {
				File[] children = objectFile.listFiles();
				if (children != null) {
					for (File file : children) {
						localObjects.add(getObjectByPath(file.getCanonicalPath()));
					}
				}
			}
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
		return localObjects;
	}

	/**
	 * Gets the resource versions by path.
	 *
	 * @param path the path
	 * @return the resource versions by path
	 * @throws RepositoryVersioningException the repository versioning exception
	 */
	public List<IResourceVersion> getResourceVersionsByPath(String path) throws RepositoryVersioningException {
		List<IResourceVersion> versions = new ArrayList<IResourceVersion>();
		String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
		String versionsPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getVersionsPath());
		File versionsDir = new File(versionsPath);
		if (versionsDir.isDirectory()) {
			File[] children = versionsDir.listFiles();

			if (children != null) {
				for (File file : children) {
					if (!LAST.equals(file.getName())) {
						int version = Integer.parseInt(file.getName());
						LocalResourceVersion localResourceVersion = new LocalResourceVersion(getRepository(), new RepositoryPath(path), version);
						versions.add(localResourceVersion);
					}
				}
				Collections.sort(versions);
			}
		}
		return versions;
	}

	/**
	 * Gets the file version by path.
	 *
	 * @param path the path
	 * @param version the version
	 * @return the file version by path
	 * @throws RepositoryVersioningException the repository versioning exception
	 */
	public LocalFileVersion getFileVersionByPath(String path, int version) throws RepositoryVersioningException {
		String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
		return getLocalFileVersionByPath(version, workspacePath);
	}

	/**
	 * Gets the local file version by path.
	 *
	 * @param version the version
	 * @param workspacePath the workspace path
	 * @return the local file version by path
	 * @throws RepositoryVersioningException the repository versioning exception
	 */
	private LocalFileVersion getLocalFileVersionByPath(int version, String workspacePath) throws RepositoryVersioningException {
		String versionsPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getVersionsPath());
		String versionPath = versionsPath + File.separator + version;
		try {
			if (FileSystemUtils.fileExists(versionPath)) {
				byte[] bytes = FileSystemUtils.loadFile(versionPath);
				if (bytes != null) {
					String ext = FilenameUtils.getExtension(workspacePath);
					String contentType = ContentTypeHelper.getContentType(ext);
					boolean isBinary = ContentTypeHelper.isBinary(contentType);
					LocalFileVersion localFileVersion = new LocalFileVersion(getRepository(), isBinary, contentType, version, bytes);
					localFileVersion.setCreatedBy(FileSystemUtils.getOwner(workspacePath));
					localFileVersion.setCreatedAt(FileSystemUtils.getModifiedAt(workspacePath));
					return localFileVersion;
				}
			}
		} catch (IOException e) {
			throw new RepositoryVersioningException(e);
		}

		return null;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	private String getUser() {
		return UserFacade.getName();
	}

}
