package org.eclipse.dirigible.core.workspace.service;

import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.repository.api.ICollection;

public class Project extends Folder implements IProject {

	public Project(ICollection projectCollection) {
		super(projectCollection);
	}

}
