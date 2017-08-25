package org.eclipse.dirigible.core.workspace.api;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;

public interface IFolder extends ICollection {

	public ICollection getInternal();

	public IFolder createFolder(String path);

	public boolean existsFolder(String path);

	public IFolder getFolder(String path);

	public List<IFolder> getFolders();

	public void deleteFolder(String path);

	public IFile createFile(String path, byte[] content);

	public IFile createFile(String path, byte[] content, boolean isBinary, String contentType);

	public IFile getFile(String path);

	public boolean existsFile(String path);

	public List<IFile> getFiles();

	public void deleteFile(String path);

}
