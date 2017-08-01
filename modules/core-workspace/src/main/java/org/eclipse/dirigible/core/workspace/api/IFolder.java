package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;

public interface IFolder extends ICollection {

	public ICollection getInternal();

	public IFolder createFolder(String name);

	public IFolder getFolder(String name);

	public List<IFolder> getFolders();

	public void deleteFolder(String name);

	public IFile createFile(String name, byte[] content);

	public IFile getFile(String name);

	public List<IFile> getFiles();

	public void deleteFile(String name);

}
