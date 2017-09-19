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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.auth.UserFacade;
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

public class LocalRepositoryDao {

	private static final Logger logger = LoggerFactory.getLogger(LocalRepositoryDao.class);

	private static final String LAST = "last";

	private static final String MODIFIED_AT = "modifiedAt";

	private static final String MODIFIED_BY = "modifiedBy";

	private static final String CREATED_AT = "createdAt";

	private static final String CREATED_BY = "createdBy";

	static final int OBJECT_TYPE_FOLDER = 0;
	static final int OBJECT_TYPE_DOCUMENT = 1;
	static final int OBJECT_TYPE_BINARY = 2;

	private FileSystemRepository repository;

	public LocalRepositoryDao(FileSystemRepository repository) {
		this.repository = repository;
	}

	public FileSystemRepository getRepository() {
		return this.repository;
	}

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

	private void createInitialVersion(byte[] content, String versionsPath) throws FileNotFoundException, IOException {
		FileSystemUtils.saveFile(versionsPath + File.separator + "1", content);
		FileSystemUtils.saveFile(versionsPath + File.separator + LAST, "1".getBytes(IRepository.UTF8));
	}

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

	private void removeVersions(String workspacePath) throws FileNotFoundException, IOException {
		String versionsPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getVersionsPath());
		if (FileSystemUtils.directoryExists(versionsPath)) {
			FileSystemUtils.removeFile(versionsPath);
		}
	}

	private void removeInfo(String workspacePath) throws FileNotFoundException, IOException {
		String infoPath = workspacePath.replace(getRepository().getRepositoryPath(), getRepository().getInfoPath());
		if (FileSystemUtils.fileExists(infoPath)) {
			FileSystemUtils.removeFile(infoPath);
		}
	}

	public void checkInitialized() {
		// TODO Auto-generated method stub

	}

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

	public byte[] getFileContent(LocalFile localFile) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), localFile.getPath());
			return FileSystemUtils.loadFile(workspacePath);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}
	}

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

	public void createFolder(String normalizePath) {
		try {
			String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), normalizePath);
			FileSystemUtils.createFolder(workspacePath);
		} catch (RepositoryWriteException e) {
			throw new LocalRepositoryException(e);
		}
	}

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

	public void copyFolder(String path, String newPath) {
		try {
			String workspacePathOld = LocalWorkspaceMapper.getMappedName(getRepository(), path);
			String workspacePathNew = LocalWorkspaceMapper.getMappedName(getRepository(), newPath);
			FileSystemUtils.copyFile(workspacePathOld, workspacePathNew);
			createInfo(workspacePathNew);
		} catch (IOException e) {
			throw new LocalRepositoryException(e);
		}

	}

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

	public LocalFileVersion getFileVersionByPath(String path, int version) throws RepositoryVersioningException {
		String workspacePath = LocalWorkspaceMapper.getMappedName(getRepository(), path);
		return getLocalFileVersionByPath(version, workspacePath);
	}

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

	private String getUser() {
		return UserFacade.getName();
	}

}
