/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.ui.publish;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class UiPublisher extends AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(UiPublisher.class);

	public UiPublisher() {
		super();
	}

	@Override
	public void publish(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation(), request);
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT);
			logger.debug("Copy all from " + sourceFolder.getFullPath().toString() + " to folder: " + targetContainer.getPath());
			copyAllFromTo(sourceFolder, targetContainer, request);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	public void activate(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getSandboxLocation(request), request);
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT);
			copyAllFromTo(sourceFolder, targetContainer, request);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	protected String getSandboxLocation(HttpServletRequest request) {
		return CommonIDEParameters.getWebContentSandbox(request);
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		// any file under web content folder is valid
		return checkFolderType(file);
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return CommonIDEParameters.WEB_CONTENT_CONTAINER_MAPPING;
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		return CommonIDEParameters.WEB_CONTENT_SANDBOX_MAPPING;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.WEB_CONTENT_REGISTRY_PUBLISH_LOCATION;
	}

	@Override
	public void template(IProject project, HttpServletRequest request) throws PublishException {
		// TODO Auto-generated method stub

	}

}
