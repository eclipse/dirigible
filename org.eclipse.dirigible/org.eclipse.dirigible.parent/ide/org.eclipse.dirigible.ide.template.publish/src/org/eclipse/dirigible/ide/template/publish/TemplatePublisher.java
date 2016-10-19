/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.publish;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class TemplatePublisher extends AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(TemplatePublisher.class);

	public TemplatePublisher() {
		super();
	}

	@Override
	public void publish(IProject project, HttpServletRequest request) throws PublishException {
		// nothing on publish
	}

	@Override
	public void activate(IProject project, HttpServletRequest request) throws PublishException {
		// nothing on activation
	}

	@Override
	protected String getSandboxLocation(HttpServletRequest request) {
		return null;
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.PROJECT_ROOT;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		// used in standard artifacts publishing only - hence false here
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return null;
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		return null;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return false;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.TEMPLATE_DEFINITIONS_REGISTRY_PUBLISH_LOCATION;
	}

	@Override
	public void template(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation(), request);
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.PROJECT_ROOT);
			logger.debug("Copy all from " + sourceFolder.getFullPath().toString() + " to folder: " + targetContainer.getPath());
			copyAllFromTo(sourceFolder, targetContainer, request);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}

	}

}
