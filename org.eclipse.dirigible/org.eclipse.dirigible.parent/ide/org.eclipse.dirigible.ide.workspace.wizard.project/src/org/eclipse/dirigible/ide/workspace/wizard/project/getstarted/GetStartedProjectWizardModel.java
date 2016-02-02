/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.wizard.project.getstarted;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.dual.ProjectCreatorEnhancer;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.shared.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.shared.ValidationStatus;
import org.eclipse.dirigible.ide.workspace.wizard.project.create.Messages;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;

public class GetStartedProjectWizardModel {

	private static final String ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME = Messages.NewProjectWizardModel_ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME;

	private static final String PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S = Messages.NewProjectWizardModel_PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S;

	private static final String PROJECT_WITH_THIS_NAME_ALREADY_EXISTS = Messages.NewProjectWizardModel_PROJECT_WITH_THIS_NAME_ALREADY_EXISTS;

	private static final String INVALID_PROJECT_NAME = Messages.NewProjectWizardModel_INVALID_PROJECT_NAME;

	public static final Logger logger = Logger.getLogger(GetStartedProjectWizardModel.class.getCanonicalName());

	private static final String INITIAL_LOCATION = "MyFirstProject"; //$NON-NLS-1$

	private String projectName = INITIAL_LOCATION;

	private String conflictUser;

	public GetStartedProjectWizardModel() {
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

		// create default folders
		List<IPublisher> publishers = PublishManager.getPublishers();
		for (IPublisher publisher : publishers) {
			IFolder folder = project.getFolder(publisher.getFolderType());
			folder.create(true, false, null);
		}

		ProjectCreatorEnhancer.enhance(project);

		createGetStartedArtifacts(project);

		activateAndPublish(project);

		project.refreshLocal(2, null);

		return project;
	}

	private void createGetStartedArtifacts(IProject project) {
		generateAndStoreArtifact(project, "user_books.table", "DataStructures");
		generateAndStoreArtifact(project, "user_books_lib.js", "ScriptingServices");
		generateAndStoreArtifact(project, "user_books.js", "ScriptingServices");
		generateAndStoreArtifact(project, "user_books.entity", "ScriptingServices");
		generateAndStoreArtifact(project, "user_books.html", "WebContent");
		generateAndStoreArtifact(project, "header.html", "WebContent");
		generateAndStoreArtifact(project, "footer.html", "WebContent");
		generateAndStoreArtifact(project, "main.menu", "WebContent");
		generateAndStoreArtifact(project, "index.html", "WebContent");
	}

	private void generateAndStoreArtifact(IProject project, String resource, String folder) {
		InputStream in = GetStartedProjectWizardModel.class.getResourceAsStream(resource);
		String user = CommonParameters.getUserName();
		user = user.toLowerCase();
		try {
			String content = IOUtils.toString(in, "UTF-8");
			content = content.replace("${User}", CommonUtils.toCamelCase(user));
			content = content.replace("${user}", user);
			content = content.replace("${USER}", user.toUpperCase());

			IFolder pckg = project.getFolder(folder + "/" + user + "_books");
			if (!pckg.exists()) {
				pckg.create(true, false, null);
			}
			IFile file = project.getFile(folder + "/" + user + "_books/" + resource.replace("user", user));
			file.create(IOUtils.toInputStream(content), true, null);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (CoreException e) {
			logger.error(e.getMessage(), e);
		}

	}

	private void activateAndPublish(IProject project) {
		try {
			PublishManager.publishProject(project);
		} catch (PublishException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
