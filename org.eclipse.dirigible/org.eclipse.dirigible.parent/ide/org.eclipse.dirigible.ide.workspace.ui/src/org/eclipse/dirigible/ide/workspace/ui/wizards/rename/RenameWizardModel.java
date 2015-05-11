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

package org.eclipse.dirigible.ide.workspace.ui.wizards.rename;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.shared.ValidationStatus;

public class RenameWizardModel {

	private static final String COULD_NOT_RENAME_RESOURCE = Messages.RenameWizardModel_COULD_NOT_RENAME_RESOURCE;

	private static final String A_RESOURCE_WITH_THIS_NAME_ALREADY_EXISTS = Messages.RenameWizardModel_A_RESOURCE_WITH_THIS_NAME_ALREADY_EXISTS;

	private static final String INVALID_RESOURCE_NAME = Messages.RenameWizardModel_INVALID_RESOURCE_NAME;

	private final IResource resource;

	private String resourceName;

	public RenameWizardModel(IResource resource) {
		this.resource = resource;
		this.resourceName = resource.getName();
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public ValidationStatus validate() {
		final IWorkspace workspace = WorkspaceLocator.getWorkspace();
		final IStatus nameValidation = workspace.validateName(resourceName,
				resource.getType());
		if (!nameValidation.isOK()) {
			return ValidationStatus.createError(INVALID_RESOURCE_NAME);
		}
		final IWorkspaceRoot root = workspace.getRoot();
		final IPath parentPath = resource.getParent().getFullPath();
		final IResource existing = root.findMember(parentPath
				.append(resourceName));
		if (existing != null) {
			return ValidationStatus
					.createError(A_RESOURCE_WITH_THIS_NAME_ALREADY_EXISTS);
		}
		return ValidationStatus.createOk();
	}

	public void persist() throws IOException {
		final IPath destination = resource.getParent().getFullPath()
				.append(resourceName);
		try {
			resource.move(destination, false, null);
		} catch (CoreException ex) {
			throw new IOException(COULD_NOT_RENAME_RESOURCE, ex);
		}
	}

}
