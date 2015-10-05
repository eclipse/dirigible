/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.wizard.project.create;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.dual.ProjectCreatorEnhancer;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.shared.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.shared.ValidationStatus;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.logging.Logger;

public class NewProjectWizardModel {

	private static final String ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME = Messages.NewProjectWizardModel_ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME;

	private static final String PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S = Messages.NewProjectWizardModel_PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S;

	private static final String PROJECT_WITH_THIS_NAME_ALREADY_EXISTS = Messages.NewProjectWizardModel_PROJECT_WITH_THIS_NAME_ALREADY_EXISTS;

	private static final String INVALID_PROJECT_NAME = Messages.NewProjectWizardModel_INVALID_PROJECT_NAME;

	public static final Logger logger = Logger.getLogger(NewProjectWizardModel.class.getCanonicalName());

	private static final String INITIAL_LOCATION = "project"; //$NON-NLS-1$

	private String projectName = INITIAL_LOCATION;

	private String conflictUser;

	private ProjectTemplateType template;

	private boolean useTemplate = true;

	public NewProjectWizardModel() {
		super();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String location) {
		this.projectName = location;
	}

	public IValidationStatus validate() {
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IStatus pathValidation = workspace.validateName(projectName, IResource.PROJECT);
		if (!pathValidation.isOK()) {
			return ValidationStatus.createError(INVALID_PROJECT_NAME);
		}

		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(projectName);
		if (project.exists()) {
			return ValidationStatus.createError(PROJECT_WITH_THIS_NAME_ALREADY_EXISTS);
		}

		if (!isValidRepositoryProject()) {
			return ValidationStatus.createError(String.format(PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S, projectName, conflictUser));
		}
		return ValidationStatus.createOk();
	}

	private boolean isValidRepositoryProject() {
		IRepository repository = RepositoryFacade.getInstance().getRepository();
		ICollection userFolders = repository.getCollection(IRepositoryPaths.DB_DIRIGIBLE_USERS);
		boolean isValid = true;
		try {
			for (ICollection user : userFolders.getCollections()) {
				if (user.exists()) {
					ICollection workspace = user.getCollection(IRepositoryPaths.WORKSPACE_FOLDER_NAME);
					for (ICollection nextProject : workspace.getCollections()) {
						if (nextProject.exists()) {
							if (nextProject.getName().equals(projectName)) {
								this.conflictUser = user.getName();
								isValid = false;
								break;
							}
						}
					}
					if (!isValid) {
						break;
					}
				}
			}
		} catch (IOException e) {
			String message = ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME;
			logger.error(message, e);
		}
		return isValid;
	}

	public IProject execute() throws CoreException {
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(projectName);
		// create the project first
		try {
			project.create(null);
		} catch (CoreException e) {
			logger.error(e.getMessage(), e);
		}

		project.open(null);

		if (isUseTemplate()) {
			String contentPath = this.template.getContentPath();
			try {
				IRepository repository = RepositoryFacade.getInstance().getRepository();
				org.eclipse.dirigible.repository.api.IResource contentResource = repository.getResource(contentPath);
				if (contentResource.exists()) {
					byte[] data = contentResource.getContent();
					IPath location = project.getRawLocation();
					if (location == null) {
						location = project.getLocation();
					}
					Map<String, String> filter = new HashMap<String, String>();
					filter.put("PROJECT_NAME", projectName);
					repository.importZip(data, location.toString(), false, false, filter);
					// repository.importZip(data, location.toString());
				}
			} catch (RepositoryException e) {
				logger.error(e.getMessage(), e);
				throw new CoreException(new Status(IStatus.ERROR, // NOPMD
						"org.eclipse.dirigible.ide.workspace.ui", e.getMessage())); //$NON-NLS-1$ // NOPMD
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new CoreException(new Status(IStatus.ERROR, // NOPMD
						"org.eclipse.dirigible.ide.workspace.ui", e.getMessage())); //$NON-NLS-1$ // NOPMD
			}

		} else {
			// create default folders
			List<IPublisher> publishers = PublishManager.getPublishers();
			for (IPublisher publisher : publishers) {
				IFolder folder = project.getFolder(publisher.getFolderType());
				folder.create(true, false, null);
			}
		}

		ProjectCreatorEnhancer.enhance(project);

		project.refreshLocal(2, null);

		return project;
	}

	public ProjectTemplateType getTemplate() {
		return template;
	}

	public void setTemplate(ProjectTemplateType template) {
		this.template = template;
	}

	public String getTemplateLocation() {
		if (this.template == null) {
			return null;
		}
		return this.template.getLocation();
	}

	public void setUseTemplate(boolean b) {
		this.useTemplate = b;
	}

	public boolean isUseTemplate() {
		return useTemplate;
	}

}
