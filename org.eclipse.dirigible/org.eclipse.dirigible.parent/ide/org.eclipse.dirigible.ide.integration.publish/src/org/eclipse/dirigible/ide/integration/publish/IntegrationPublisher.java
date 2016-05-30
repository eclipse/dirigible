/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.integration.publish;

import static org.eclipse.dirigible.ide.integration.publish.IntegrationConstants.IS_CONTENT_FOLDER;
import static org.eclipse.dirigible.ide.integration.publish.IntegrationConstants.IS_REGISTYRY_PUBLISH_LOCATION;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.listener.ListenersUpdater;

/**
 * Publisher for artifacts in IntegrationService folder.
 */
public class IntegrationPublisher extends AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(IntegrationPublisher.class);

	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project, IS_CONTENT_FOLDER);
			copyAllFromTo(sourceFolder, targetContainer);

			ListenersUpdater listenersUpdater = new ListenersUpdater(RepositoryFacade.getInstance().getRepository(),
					DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()), getRegistryLocation());
			listenersUpdater.applyUpdates();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	// no sandboxing for integration services
	@Override
	public void activate(IProject project) throws PublishException {
		publish(project);
	}

	@Override
	public void activateFile(IFile file) throws PublishException {
		publish(file.getProject());
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (ICommonConstants.ARTIFACT_EXTENSION.FLOW.equals(file.getFileExtension())
					|| ICommonConstants.ARTIFACT_EXTENSION.JOB.equals(file.getFileExtension())
					|| ICommonConstants.ARTIFACT_EXTENSION.LISTENER.equals(file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		if (ICommonConstants.ARTIFACT_EXTENSION.FLOW.equals(file.getFileExtension())) {
			return CommonIDEParameters.FLOW_CONTAINER_MAPPING;
		}
		if (ICommonConstants.ARTIFACT_EXTENSION.JOB.equals(file.getFileExtension())) {
			return CommonIDEParameters.JOB_CONTAINER_MAPPING;
		}
		if (ICommonConstants.ARTIFACT_EXTENSION.LISTENER.equals(file.getFileExtension())) {
			return CommonIDEParameters.LISTENER_CONTAINER_MAPPING;
		}
		return null;
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		if (ICommonConstants.ARTIFACT_EXTENSION.FLOW.equals(file.getFileExtension())) {
			return CommonIDEParameters.FLOW_SANDBOX_MAPPING;
		}
		if (ICommonConstants.ARTIFACT_EXTENSION.JOB.equals(file.getFileExtension())) {
			return CommonIDEParameters.JOB_SANDBOX_MAPPING;
		}
		if (ICommonConstants.ARTIFACT_EXTENSION.JOB.equals(file.getFileExtension())) {
			return CommonIDEParameters.LISTENER_SANDBOX_MAPPING;
		}
		return null;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getSandboxLocation() {
		return CommonIDEParameters.getIntegrationContentSandbox();
	}

	@Override
	protected String getRegistryLocation() {
		return IS_REGISTYRY_PUBLISH_LOCATION;
	}

}
