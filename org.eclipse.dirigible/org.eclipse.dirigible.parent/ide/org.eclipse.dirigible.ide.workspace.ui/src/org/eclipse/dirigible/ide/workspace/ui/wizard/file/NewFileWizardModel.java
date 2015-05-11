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

package org.eclipse.dirigible.ide.workspace.ui.wizard.file;

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.shared.IContentProvider;
import org.eclipse.dirigible.ide.workspace.ui.shared.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.shared.TextContentProvider;
import org.eclipse.dirigible.ide.workspace.ui.shared.ValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewerUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class NewFileWizardModel {

	private static final String COULD_NOT_READ_FILE_CONTENT = Messages.NewFileWizardModel_COULD_NOT_READ_FILE_CONTENT;

	private static final String A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS = Messages.NewFileWizardModel_A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS;

	private static final String INVALID_FILE_NAME = Messages.NewFileWizardModel_INVALID_FILE_NAME;

	private static final String INVALID_PARENT_PATH = Messages.NewFileWizardModel_INVALID_PARENT_PATH;

	private static final String CONTENT_PROVIDER_CANNOT_BE_NULL = Messages.NewFileWizardModel_CONTENT_PROVIDER_CANNOT_BE_NULL;

	private static final String PARENT_LOCATION_CANNOT_BE_NULL = Messages.NewFileWizardModel_PARENT_LOCATION_CANNOT_BE_NULL;

	private static final String FILE_NAME_CANNOT_BE_NULL = Messages.NewFileWizardModel_FILE_NAME_CANNOT_BE_NULL;

	private String fileName = "file.txt"; //$NON-NLS-1$

	private String parentLocation = ""; //$NON-NLS-1$

	private IContentProvider contentProvider = new TextContentProvider();
	
	private static final Logger logger = Logger.getLogger(NewFileWizardModel.class);

	public NewFileWizardModel() {
		super();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name) {
		if (name == null) {
			throw new IllegalArgumentException(FILE_NAME_CANNOT_BE_NULL);
		}
		this.fileName = name;
	}

	public String getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(String location) {
		if (location == null) {
			throw new IllegalArgumentException(PARENT_LOCATION_CANNOT_BE_NULL);
		}
		this.parentLocation = location;
	}

	public IContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(IContentProvider contentProvider) {
		if (contentProvider == null) {
			throw new IllegalArgumentException(CONTENT_PROVIDER_CANNOT_BE_NULL);
		}
		this.contentProvider = contentProvider;
	}

	public IValidationStatus validate() {
		IValidationStatus locationValidation = validateResourceLocation();
		IValidationStatus contentValidation = validateResourceContent();
		if (locationValidation.hasErrors()) {
			return locationValidation;
		}
		if (contentValidation.hasErrors()) {
			return contentValidation;
		}
		String warningMessage = ""; //$NON-NLS-1$
		if (locationValidation.hasWarnings()) {
			warningMessage = warningMessage + locationValidation.getMessage()
					+ "\n\n"; //$NON-NLS-1$
		}
		if (contentValidation.hasWarnings()) {
			warningMessage = warningMessage + contentValidation.getMessage()
					+ "\n"; //$NON-NLS-1$
		}
		if (!warningMessage.isEmpty()) {
			return ValidationStatus.createWarning(warningMessage);
		}
		return ValidationStatus.createOk();
	}

	private IValidationStatus validateResourceLocation() {
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IStatus folderLocationValidation = workspace.validatePath(
				parentLocation, IResource.FOLDER);
		IStatus projectLocationValidation = workspace.validatePath(
				parentLocation, IResource.PROJECT);
		if (!folderLocationValidation.isOK()
				&& !projectLocationValidation.isOK()) {
			return ValidationStatus.createError(INVALID_PARENT_PATH);
		}
		
		
		if(parentLocation.contains(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES)
				|| parentLocation.contains(ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES)){
			if(isValidScriptingServiceFileName(fileName)){
				IWorkspaceRoot root = workspace.getRoot();
				if(isResourceExist(root)){
					return ValidationStatus
							.createError(A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS);
				}
				
				return ValidationStatus.createOk();
			} else {
				return ValidationStatus.createError(INVALID_FILE_NAME);
			}
		} else {
			IStatus nameValidation = workspace.validateName(fileName,
					IResource.FILE);
			if (!nameValidation.isOK()) {
				return ValidationStatus.createError(INVALID_FILE_NAME);
			}
			IWorkspaceRoot root = workspace.getRoot();
			if(isResourceExist(root)){
				return ValidationStatus
						.createError(A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS);
			}
			
			return ValidationStatus.createOk();
		}
	}

	private IValidationStatus validateResourceContent() {
		return contentProvider.validate();
	}

	public void execute() throws CoreException {
		IPath location = new Path(parentLocation).append(fileName);
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile file = root.getFile(location);
		try {
			file.create(contentProvider.getContent(), false, null);
			IContainer parent = file.getParent();
			if (parent != null) {
				WorkspaceViewerUtils.expandElement(parent);
			}
			WorkspaceViewerUtils.selectElement(file);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			throw new CoreException(new Status(Status.ERROR, "plugin", //$NON-NLS-1$ // NOPMD
					COULD_NOT_READ_FILE_CONTENT)); // NOPMD
		}
	}
	
	private boolean isValidScriptingServiceFileName(String fileName){
		
		String scriptingServicefileRegExPattern = "([a-zA-Z_0-9]+)+([\\.]){0,1}(([a-zA-Z0-9]*)*)"; //$NON-NLS-1$
		if (Pattern.matches(scriptingServicefileRegExPattern, fileName)) {
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean isResourceExist(IWorkspaceRoot root){
		IPath location = new Path(parentLocation).append(fileName);
		IResource resource = root.findMember(location.toString());
		if (resource != null) {
			return true;
		}
		else{
			return false;
		}
	}
}
