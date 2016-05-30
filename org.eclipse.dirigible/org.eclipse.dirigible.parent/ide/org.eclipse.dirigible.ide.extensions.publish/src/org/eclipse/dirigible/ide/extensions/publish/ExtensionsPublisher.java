/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.extensions.publish;

import static org.eclipse.dirigible.ide.extensions.publish.ExtensionsConstants.REGISTYRY_PUBLISH_LOCATION;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.dirigible.repository.ext.extensions.ExtensionUpdater;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class ExtensionsPublisher extends AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(ExtensionsPublisher.class);

	public ExtensionsPublisher() {
		super();
	}

	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS);
			copyAllFromTo(sourceFolder, targetContainer);

			List<String> knownFiles = new ArrayList<String>();
			ExtensionUpdater extensionUpdater = new ExtensionUpdater(RepositoryFacade.getInstance().getRepository(),
					DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()), getRegistryLocation(),
					CommonIDEParameters.getRequest());

			// # 177
			// extensionUpdater.enumerateKnownFiles(targetContainer, knownFiles);

			ICollection sourceProjectContainer = getSourceProjectContainer(project);
			ICollection sourceContainer = sourceProjectContainer.getCollection(ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS);
			extensionUpdater.enumerateKnownFiles(sourceContainer, knownFiles);

			List<String> errors = new ArrayList<String>();
			extensionUpdater.executeUpdate(knownFiles, CommonIDEParameters.getRequest(), errors);
			if (errors.size() > 0) {
				throw new PublishException(CommonUtils.concatenateListOfStrings(errors, "\n"));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	// no sandboxing for extension points
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
		return ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (ExtensionUpdater.EXTENSION_EXTENSION.equals("." + file.getFileExtension()) //$NON-NLS-1$
					|| ExtensionUpdater.EXTENSION_EXTENSION_POINT.equals("." + file.getFileExtension())) { //$NON-NLS-1$
				return true;
			}
		}
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
	protected String getSandboxLocation() {
		return null;
	}

	@Override
	protected String getRegistryLocation() {
		return REGISTYRY_PUBLISH_LOCATION;
	}
}
