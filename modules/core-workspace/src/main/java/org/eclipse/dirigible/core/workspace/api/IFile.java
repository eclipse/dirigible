package org.eclipse.dirigible.core.workspace.api;

import org.eclipse.dirigible.repository.api.IResource;

public interface IFile extends IResource {

	public IResource getInternal();

}
