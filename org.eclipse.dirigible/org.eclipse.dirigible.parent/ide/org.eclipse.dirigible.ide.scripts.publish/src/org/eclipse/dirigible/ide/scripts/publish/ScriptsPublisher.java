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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ScriptsPublisher extends AbstractPublisher implements IPublisher {

	/**
	 * List of extensions recognized by the Scripting Services Publisher
	 */
	public static final List<String> RECOGNIZED_EXTENSIONS = new ArrayList<String>();

	/**
	 * Map of the artifacts' URL locations by extension, for published resources (Registry)
	 */
	public static final Map<String, String> PUBLISH_CONTAINERS = new HashMap<String, String>();

	/**
	 * Map of the artifacts' URL locations by extension, for activated resources (Sandbox)
	 */
	public static final Map<String, String> ACTIVATE_CONTAINERS = new HashMap<String, String>();

	static {
		// recognized extensions
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.JSON);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.SWAGGER);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.ENTITY);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.RUBY);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.GROOVY);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.JAVA);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.COMMAND);
		RECOGNIZED_EXTENSIONS.add(ICommonConstants.ARTIFACT_EXTENSION.SQL);
		// put your extension here...

		// URL container per extension - publish
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT, CommonIDEParameters.JAVASCRIPT_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JSON, CommonIDEParameters.JAVASCRIPT_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.SWAGGER, CommonIDEParameters.JAVASCRIPT_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.ENTITY, CommonIDEParameters.JAVASCRIPT_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.RUBY, CommonIDEParameters.RUBY_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.GROOVY, CommonIDEParameters.GROOVY_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JAVA, CommonIDEParameters.JAVA_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.COMMAND, CommonIDEParameters.COMMAND_CONTAINER_MAPPING);
		PUBLISH_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.SQL, CommonIDEParameters.SQL_CONTAINER_MAPPING);
		// put your mapping here...

		// URL container per extension - activate
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT, CommonIDEParameters.JAVASCRIPT_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JSON, CommonIDEParameters.JAVASCRIPT_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.SWAGGER, CommonIDEParameters.JAVASCRIPT_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.ENTITY, CommonIDEParameters.JAVASCRIPT_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.RUBY, CommonIDEParameters.RUBY_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.GROOVY, CommonIDEParameters.GROOVY_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.JAVA, CommonIDEParameters.JAVA_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.COMMAND, CommonIDEParameters.COMMAND_SANDBOX_MAPPING);
		ACTIVATE_CONTAINERS.put(ICommonConstants.ARTIFACT_EXTENSION.SQL, CommonIDEParameters.SQL_SANDBOX_MAPPING);
		// put your mapping here...
	}

	/**
	 * The publisher for the Scripting Services
	 */
	public ScriptsPublisher() {
		super();
	}

	/**
	 * Actual Publish method for Scripting Services
	 *
	 * @param project
	 * @throws PublishException
	 */
	@Override
	public void publish(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(getRegistryLocation(), request);
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
			copyAllFromTo(sourceFolder, targetContainer, request);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	/**
	 * Actual Activate method for Scripting Services
	 *
	 * @param project
	 * @throws PublishException
	 */
	@Override
	public void activate(IProject project, HttpServletRequest request) throws PublishException {
		try {
			final ICollection targetContainer = getTargetProjectContainer(CommonIDEParameters.getScriptingContentSandbox(request), request);
			final IFolder sourceFolder = getSourceFolder(project, ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
			copyAllFromTo(sourceFolder, targetContainer, request);
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	/**
	 * Name of the predefined folder for Scripting Services
	 *
	 * @return name of the folder
	 */
	@Override
	public String getFolderType() {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

	/**
	 * Checks whether the artifact is recognized as a Scripting Service or not
	 *
	 * @param file
	 * @return true if recognized and false otherwise
	 */
	@Override
	public boolean recognizedFile(IFile file) {
		if (checkFolderType(file)) {
			final String ext = file.getFileExtension();
			if (RECOGNIZED_EXTENSIONS.contains(ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPublishedContainerMapping(IFile file) {
		final String ext = file.getFileExtension();
		return PUBLISH_CONTAINERS.get(ext);
	}

	@Override
	public String getActivatedContainerMapping(IFile file) {
		final String ext = file.getFileExtension();
		return ACTIVATE_CONTAINERS.get(ext);
	}

	@Override
	public boolean isAutoActivationAllowed() {
		return true;
	}

	@Override
	protected String getSandboxLocation(HttpServletRequest request) {
		return CommonIDEParameters.getScriptingContentSandbox(request);
	}

	@Override
	public String getDebugEndpoint(IFile file) {
		if (ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT.equals(file.getFileExtension())) {
			return CommonIDEParameters.getServicesUrl() + CommonIDEParameters.JAVASCRIPT_DEBUG_CONTAINER_MAPPING + generatePublishedPath(file);
		}
		return null;
	}

	@Override
	protected String getRegistryLocation() {
		return ICommonConstants.SCRIPTING_REGISTRY_PUBLISH_LOCATION;
	}

	@Override
	public void template(IProject project, HttpServletRequest request) throws PublishException {

	}

}
