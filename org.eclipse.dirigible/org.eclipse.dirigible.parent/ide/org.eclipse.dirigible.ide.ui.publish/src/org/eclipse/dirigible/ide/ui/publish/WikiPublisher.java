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

package org.eclipse.dirigible.ide.ui.publish;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class WikiPublisher extends AbstractPublisher implements IPublisher {

	public WikiPublisher() {
		super();
	}
	
	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(
					project,
					getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void activate(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(
					project,
					CommonParameters.getWikiContentSandbox());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}
	
	@Override
	protected String getSandboxLocation() {
		return CommonParameters.getWikiContentSandbox();
	}
	
	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		// any file under web content folder is valid
		return checkFolderType(file);
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return CommonParameters.WIKI_CONTENT_CONTAINER_MAPPING;
	}
	
	@Override
	public String getActivatedContainerMapping(IFile file) {
		return CommonParameters.WIKI_CONTENT_SANDBOX_MAPPING;
	}
	
	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.WIKI_CONTENT_REGISTRY_PUBLISH_LOCATION;
	}

}
