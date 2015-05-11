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

package org.eclipse.dirigible.ide.scripts.publish;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class ScriptsPublisher extends AbstractPublisher implements IPublisher {

	public ScriptsPublisher() {
		super();
	}

	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(project,
					getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	public void activate(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(project,
					CommonParameters.getScriptingContentSandbox());
			final IFolder sourceFolder = getSourceFolder(project,
					ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (CommonParameters.JAVASCRIPT_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())
					|| CommonParameters.RUBY_SERVICE_EXTENSION
							.equals(ICommonConstants.DOT + file.getFileExtension())
					|| CommonParameters.GROOVY_SERVICE_EXTENSION.equals(ICommonConstants.DOT
							+ file.getFileExtension())
					|| CommonParameters.JAVA_SERVICE_EXTENSION.equals(ICommonConstants.DOT
							+ file.getFileExtension())
					|| CommonParameters.COMMAND_SERVICE_EXTENSION.equals(ICommonConstants.DOT
							+ file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		if (CommonParameters.JAVASCRIPT_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.JAVASCRIPT_CONTAINER_MAPPING;
		}
		if (CommonParameters.RUBY_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.RUBY_CONTAINER_MAPPING;
		}
		if (CommonParameters.GROOVY_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.GROOVY_CONTAINER_MAPPING;
		}
		if (CommonParameters.JAVA_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.JAVA_CONTAINER_MAPPING;
		}
		if (CommonParameters.COMMAND_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.COMMAND_CONTAINER_MAPPING;
		}
		return null;
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		if (CommonParameters.JAVASCRIPT_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.JAVASCRIPT_SANDBOX_MAPPING;
		}
		if (CommonParameters.RUBY_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.RUBY_SANDBOX_MAPPING;
		}
		if (CommonParameters.GROOVY_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.GROOVY_SANDBOX_MAPPING;
		}
		if (CommonParameters.JAVA_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.JAVA_SANDBOX_MAPPING;
		}
		if (CommonParameters.COMMAND_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.COMMAND_SANDBOX_MAPPING;
		}
		return null;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getSandboxLocation() {
		return CommonParameters.getScriptingContentSandbox();
	}

	@Override
	public String getDebugEndpoint(IFile file) {
		if (CommonParameters.JAVASCRIPT_SERVICE_EXTENSION.equals(ICommonConstants.DOT + file.getFileExtension())) {
			return CommonParameters.getServicesUrl()
					+ CommonParameters.JAVASCRIPT_DEBUG_CONTAINER_MAPPING
					+ generatePublishedPath(file);
		}
		return null;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.SCRIPTING_REGISTRY_PUBLISH_LOCATION;
	}

}
