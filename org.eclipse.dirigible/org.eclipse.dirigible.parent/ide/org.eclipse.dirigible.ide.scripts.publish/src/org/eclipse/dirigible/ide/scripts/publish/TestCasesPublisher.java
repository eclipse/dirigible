/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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

public class TestCasesPublisher extends AbstractPublisher implements IPublisher {

	public TestCasesPublisher() {
		super();
	}

	@Override
	public void publish(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(project, getRegistryLocation());
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.TEST_CASES);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	public void activate(IProject project) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(project, CommonParameters.getTestingContentSandbox());
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.TEST_CASES);
			copyAllFromTo(sourceFolder, targetContainer);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	protected String getSandboxLocation() {
		return CommonParameters.getTestingContentSandbox();
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.TEST_CASES;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT.equals("." //$NON-NLS-1$
					+ file.getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		return CommonParameters.TEST_CASES_CONTAINER_MAPPING;
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		return CommonParameters.TEST_CASES_SANDBOX_MAPPING;
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.TESTS_REGISTRY_PUBLISH_LOCATION;
	}

}
