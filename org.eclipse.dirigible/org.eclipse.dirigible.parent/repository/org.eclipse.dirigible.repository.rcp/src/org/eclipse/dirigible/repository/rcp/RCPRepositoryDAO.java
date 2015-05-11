/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.rcp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.repository.api.ContentTypeHelper;

public class RCPRepositoryDAO {
	
	static final int OBJECT_TYPE_FOLDER = 0;
	static final int OBJECT_TYPE_DOCUMENT = 1;
	static final int OBJECT_TYPE_BINARY = 2;
	
	private RCPRepository repository;

	RCPRepositoryDAO(RCPRepository repository) {
		this.repository = repository;
	}
	
	public RCPRepository getRepository() {
		return this.repository;
	}
	
	public void createFile(String path, byte[] content, boolean isBinary,
			String contentType) throws RCPBaseException {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(path);
			FileUtils.saveFile(workspacePath, content);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
		
	}

	public void checkInitialized() {
		// TODO Auto-generated method stub
		
	}

	public void setFileContent(RCPFile rcpFile, byte[] content) {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(rcpFile.getPath());
			FileUtils.saveFile(workspacePath, content);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
	}

	public byte[] getFileContent(RCPFile rcpFile) {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(rcpFile.getPath());
			return FileUtils.loadFile(workspacePath);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
	}

	public void renameFile(String path, String newPath) {
		
		try {
			String workspacePathOld = RCPWorkspaceMapper.getMappedName(path);
			String workspacePathNew = RCPWorkspaceMapper.getMappedName(newPath);
			FileUtils.moveFile(workspacePathOld, workspacePathNew);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
	}

	public void removeFileByPath(String path) {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(path);
			FileUtils.removeFile(workspacePath);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}		
	}

	public void removeFolderByPath(String path) {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(path);
			FileUtils.removeFile(workspacePath);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}		
	}

	public void createFolder(String normalizePath) {
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(normalizePath);
			FileUtils.createFolder(workspacePath);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}		
	}

	public void renameFolder(String path, String newPath) {
		
		try {
			String workspacePathOld = RCPWorkspaceMapper.getMappedName(path);
			String workspacePathNew = RCPWorkspaceMapper.getMappedName(newPath);
			FileUtils.moveFile(workspacePathOld, workspacePathNew);
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
		
	}

	public RCPObject getObjectByPath(String path) {
		
		RCPObject rcpObject = null; 
				
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(path);
			File objectFile = new File(workspacePath);
			if (!objectFile.exists()) {
				// This is folder, that was not created
				if (ContentTypeHelper.getExtension(workspacePath).isEmpty()) {
					FileUtils.createFolder(workspacePath);
				}
			}
			if (objectFile.isFile()) {
				String contentType = ContentTypeHelper.getContentType(FileUtils.getExtension(workspacePath));
				rcpObject = new RCPFile(repository, ContentTypeHelper.isBinary(contentType), contentType);
//				FileUtils.createFile(workspacePath);
			} else {
				rcpObject = new RCPFolder(repository);
//				FileUtils.createFolder(workspacePath);
			}
			rcpObject.setName(objectFile.getName());
			rcpObject.setPath(workspacePath);
			// TODO createBy and createAt exists?
			rcpObject.setCreatedBy(FileUtils.getOwner(workspacePath));
			rcpObject.setCreatedAt(FileUtils.getModifiedAt(workspacePath));
			rcpObject.setModifiedBy(FileUtils.getOwner(workspacePath));
			rcpObject.setModifiedAt(FileUtils.getModifiedAt(workspacePath));
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
		return rcpObject;
	}

	public List<RCPObject> getChildrenByFolder(String path) {
		List<RCPObject> rcpObjects = new ArrayList<RCPObject>();
		try {
			String workspacePath = RCPWorkspaceMapper.getMappedName(path);
			File objectFile = new File(workspacePath);
			if (objectFile.isDirectory()) {
				File[] children = objectFile.listFiles();
				if (children != null) {
					for (File file : children) {
						rcpObjects.add(getObjectByPath(file.getCanonicalPath()));
					}
				}
			}
		} catch (IOException e) {
			throw new RCPBaseException(e);
		}
		return rcpObjects;
	}


}
