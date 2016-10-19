/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.security.publish;

import static org.eclipse.dirigible.ide.security.publish.SecurityConstants.REGISTYRY_PUBLISH_LOCATION;
import static org.eclipse.dirigible.ide.security.publish.SecurityConstants.SC_CONTENT_FOLDER;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.publish.AbstractPublisher;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.SecurityUpdater;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class SecurityPublisher extends AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(SecurityPublisher.class);

	public SecurityPublisher() {
		super();
	}

	@Override
	public void publish(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation(), request);
			final IFolder sourceFolder = getSourceFolder(project, SC_CONTENT_FOLDER);
			copyAllFromTo(sourceFolder, targetContainer, request);

			List<String> knownFiles = new ArrayList<String>();
			SecurityUpdater securityUpdater = new SecurityUpdater(RepositoryFacade.getInstance().getRepository(request),
					DataSourceFacade.getInstance().getDataSource(request), getRegistryLocation());

			// # 177
			// securityUpdater.enumerateKnownFiles(targetContainer, knownFiles);
			ICollection sourceProjectContainer = getSourceProjectContainer(project, request);
			ICollection sourceContainer = sourceProjectContainer.getCollection(ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS);
			securityUpdater.enumerateKnownFiles(sourceContainer, knownFiles);

			List<String> errors = new ArrayList<String>();
			securityUpdater.executeUpdate(knownFiles, request, errors);
			if (errors.size() > 0) {
				throw new PublishException(CommonUtils.concatenateListOfStrings(errors, "\n"));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	// no sandboxing for integration services
	@Override
	public void activate(IProject project, HttpServletRequest request) throws PublishException {
		publish(project, request);
	}

	@Override
	public void activateFile(IFile file, HttpServletRequest request) throws PublishException {
		publish(file.getProject(), request);
	}

	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;
	}

	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			if (SecurityUpdater.EXTENSION_ACCESS.equals("." //$NON-NLS-1$
					+ file.getFileExtension())) {
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
	protected String getSandboxLocation(HttpServletRequest request) {
		return null;
	}

	@Override
	protected String getRegistryLocation() {
		return REGISTYRY_PUBLISH_LOCATION;
	}

	@Override
	public void template(IProject project, HttpServletRequest request) throws PublishException {

	}
}
