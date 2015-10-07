/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import java.io.InputStream;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.ui.common.validation.ValidationStatus;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class GenerationModel {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final String COULD_NOT_OPEN_INPUT_STREAM_FOR = Messages.GenerationModel_COULD_NOT_OPEN_INPUT_STREAM_FOR;

	private static final String TEMPLATE_LOCATION_IS_EMPTY = Messages.GenerationModel_TEMPLATE_LOCATION_IS_EMPTY;

	private static final String RESOURCE_ALREADY_EXISTS_IN_THE_WORKSPACE = Messages.GenerationModel_RESOURCE_ALREADY_EXISTS_IN_THE_WORKSPACE;

	private static final String NAME_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S = Messages.GenerationModel_NAME_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S;

	private static final String PATH_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S = Messages.GenerationModel_PATH_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S;

	private static final Logger logger = Logger.getLogger(GenerationModel.class);

	private IResource sourceResource;

	// private String targetLocation;

	private String targetContainer;

	private String packageName;

	private String fileName;

	private TemplateType template;

	private Class<?> templateClassLoader;

	public IResource getSourceResource() {
		return sourceResource;
	}

	public void setSourceResource(IResource sourceResource) {
		this.sourceResource = sourceResource;
	}

	public String getTargetLocation() {
		// return targetLocation;
		if (this.targetContainer == null) {
			return null;
		}
		if (getPackageName() == null) {
			return this.targetContainer;
		}
		return this.targetContainer + IRepository.SEPARATOR + getPackageName();
	}

	// public void setTargetLocation(String targetLocation) {
	// this.targetLocation = targetLocation;
	// }

	public String getTargetContainer() {
		return targetContainer;
	}

	public void setTargetContainer(String targetContainer) {
		this.targetContainer = targetContainer;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		if ((this.packageName != null) && (this.packageName.length() > 1) && this.packageName.startsWith(IRepository.SEPARATOR)) {
			this.packageName = this.packageName.substring(1);
		}
		this.packageName = packageName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTemplateLocation() {
		if (template != null) {
			return template.getLocation();
		}
		return null;
	}

	public String getTemplateExtension() {
		if (template != null) {
			return template.getExtension();
		}
		return null;
	}

	public TemplateType getTemplate() {
		return template;
	}

	public void setTemplate(TemplateType template) {
		this.template = template;
	}

	public IValidationStatus validateLocationGeneric() {
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		return validateLocationGeneric(workspace);
	}

	public IValidationStatus validateLocationGeneric(IWorkspace workspace) {

		IStatus folderLocationValidation = workspace.validatePath(getTargetLocation(), IResource.FOLDER);
		IStatus projectLocationValidation = workspace.validatePath(getTargetLocation(), IResource.PROJECT);
		if (!folderLocationValidation.isOK() && !projectLocationValidation.isOK()) {
			return ValidationStatus.createError(PATH_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S);
		}

		if (getTargetLocation().contains(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES)) {

			if (isValidScriptingServiceFileName(getFileName())) {
				IWorkspaceRoot root = workspace.getRoot();
				if (isResourceExist(root)) {
					IResource res = extractResource(root);
					return ValidationStatus.createError(String.format(RESOURCE_ALREADY_EXISTS_IN_THE_WORKSPACE, res.getFullPath().toString()));
				}
				return ValidationStatus.createOk();
			} else {
				return ValidationStatus.createError(NAME_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S);
			}
		} else {
			IStatus nameValidation = workspace.validateName(getFileName(), IResource.FILE);
			if (!nameValidation.isOK()) {
				return ValidationStatus.createError(NAME_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S);
			}
			IWorkspaceRoot root = workspace.getRoot();
			if (isResourceExist(root)) {
				IPath location = new Path(getTargetLocation()).append(getFileName());
				return ValidationStatus.createError(String.format(RESOURCE_ALREADY_EXISTS_IN_THE_WORKSPACE, location.toString()));
			}
			return ValidationStatus.createOk();
		}
	}

	public IValidationStatus validateTemplate() {
		if ((getTemplateLocation() == null) || EMPTY_STRING.equals(getTemplateLocation())) {
			return ValidationStatus.createError(TEMPLATE_LOCATION_IS_EMPTY);
		}
		InputStream in = null;
		if (getTemplateClassLoader() != null) {
			in = getTemplateClassLoader().getResourceAsStream(getTemplateLocation());
		} else {
			in = this.getClass().getResourceAsStream(getTemplateLocation());
		}
		if (in == null) {
			logger.error(COULD_NOT_OPEN_INPUT_STREAM_FOR + getTemplateLocation());
			return ValidationStatus.createError(String.format(COULD_NOT_OPEN_INPUT_STREAM_FOR, getTemplateLocation()));
		}
		return ValidationStatus.createOk();
	}

	protected abstract IValidationStatus validate();

	public String getFileNameNoExtension() {
		return CommonUtils.getFileNameNoExtension(fileName);
	}

	public String getProjectName() {
		StringBuilder result = new StringBuilder();
		if (getTargetContainer() == null) {
			return null;
		}
		IPath location = new Path(getTargetContainer());
		// if (location.segmentCount() > 2) {
		// for (int i = 0; i < location.segmentCount(); i++) {
		// if (i == 1) {
		// continue;
		// }
		// result.append(location.segment(i) + ICommonConstants.SEPARATOR);
		// }
		// result.delete(result.length() - ICommonConstants.SEPARATOR.length(), result.length());
		// } else {
		result.append(location.segment(0));
		// }
		return result.toString();
	}

	public Class<?> getTemplateClassLoader() {
		return templateClassLoader;
	}

	public void setTemplateClassLoader(Class<?> templateClassLoader) {
		this.templateClassLoader = templateClassLoader;
	}

	private boolean isValidScriptingServiceFileName(String fileName) {

		String scriptingServicefileRegExPattern = "([a-zA-Z_0-9]+)+([\\.]){0,1}(([a-zA-Z0-9]*)*)"; //$NON-NLS-1$
		if (Pattern.matches(scriptingServicefileRegExPattern, fileName)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isResourceExist(IWorkspaceRoot root) {
		IResource resource = extractResource(root);
		if (resource != null) {
			return true;
		} else {
			return false;
		}
	}

	private IResource extractResource(IWorkspaceRoot root) {
		IPath location = new Path(getTargetLocation()).append(getFileName());
		IResource resource = root.findMember(location.toString());
		return resource;
	}
}
