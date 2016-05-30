/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.publish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Base class for all the publishers.
 * Provide utility methods often used by its ancestors, such as mapping between Resource API and Repository API.
 */
public abstract class AbstractPublisher implements IPublisher {

	private static final Logger logger = Logger.getLogger(AbstractPublisher.class);

	protected ICollection getTargetProjectContainer(String registryLocation) throws IOException {

		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection publishContainer = repository.getCollection(registryLocation);
		// final ICollection projectContainer = publishContainer.getCollection(project.getName());
		// #177
		final ICollection projectContainer = publishContainer;
		if (projectContainer.exists()) {
			return projectContainer;
		}

		projectContainer.create();

		return projectContainer;
	}

	protected org.eclipse.dirigible.repository.api.IResource getTargetFileLocation(IFile file, String registryLocation) throws IOException {
		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection publishContainer = repository.getCollection(registryLocation);
		// final ICollection projectContainer = publishContainer.getCollection(file.getProject().getName());
		// #177
		final ICollection projectContainer = publishContainer;
		final org.eclipse.dirigible.repository.api.IResource fileResource = projectContainer
				.getResource(file.getProjectRelativePath().removeFirstSegments(1).toString());
		return fileResource;
	}

	protected IFolder getSourceFolder(IProject project, String sourceFolderName) {
		return project.getFolder(sourceFolderName);
	}

	/**
	 * Copy the artifacts from workspace to either sandbox or registry
	 *
	 * @param source
	 * @param target
	 * @throws CoreException
	 * @throws IOException
	 */
	public void copyAllFromTo(IContainer source, ICollection target) throws CoreException, IOException {

		if (!source.exists()) {
			return;
		}

		String user = getUser();

		// #177
		// synchronizeRepositoryWithWorkspace(source, target);

		for (IResource resource : source.members()) {
			if (resource instanceof IFolder) {
				copyFolderInto((IFolder) resource, target, user);
			}
			if (resource instanceof IFile) {
				copyFileInto((IFile) resource, target, user);
			}
		}
	}

	/**
	 * Utility method for getting the user
	 *
	 * @return
	 */
	public String getUser() {
		String user = CommonIDEParameters.getUserName();
		return user;
	}

	/**
	 * Copy a folder from workspace to either sandbox or registry
	 *
	 * @param folder
	 * @param target
	 * @param user
	 * @throws IOException
	 * @throws CoreException
	 */
	public void copyFolderInto(IFolder folder, ICollection target, String user) throws IOException, CoreException {
		final ICollection targetFolder = target.getCollection(folder.getName());

		if (!targetFolder.exists()) {
			targetFolder.create();
		}
		copyAllFromTo(folder, targetFolder);
	}

	/**
	 * Copy a file from workspace to either sandbox or registry
	 *
	 * @param file
	 * @param target
	 * @param user
	 * @throws IOException
	 * @throws CoreException
	 */
	public void copyFileInto(IFile file, ICollection target, String user) throws IOException, CoreException {

		String fileLocation = file.getFullPath().toString();
		String projectLocation = file.getProject().getFullPath().toString();

		final org.eclipse.dirigible.repository.api.IResource targetResource = target.getRepository()
				.getResource(target.getPath() + IRepository.SEPARATOR + file.getName());

		org.eclipse.dirigible.repository.api.IResource resource = target.getRepository()
				.getResource(file.getWorkspace().getRoot().getRawLocation() + file.getFullPath().toString());

		if (targetResource.exists()) {
			Date targetResourceLastModifiedAt = targetResource.getInformation().getModifiedAt();
			Date resourceLastModifiedAt = resource.getInformation().getModifiedAt();

			if ((resourceLastModifiedAt != null) && (targetResourceLastModifiedAt != null)) {
				if (resourceLastModifiedAt.getTime() >= targetResourceLastModifiedAt.getTime()) {
					setTargetResourceContent(file, targetResource, resource);
				}
			} else {
				setTargetResourceContent(file, targetResource, resource);
			}

		} else {
			setTargetResourceContent(file, targetResource, resource);
		}

	}

	private void setTargetResourceContent(IFile file, final org.eclipse.dirigible.repository.api.IResource targetResource,
			org.eclipse.dirigible.repository.api.IResource resource) throws IOException, CoreException {
		targetResource.setContent(readFile(file), resource.isBinary(), resource.getContentType());
	}

	protected byte[] readFile(IFile file) throws IOException, CoreException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final InputStream in = file.getContents();
		try {
			IOUtils.copy(in, out);
		} finally {
			in.close();
		}
		return out.toByteArray();
	}

	protected String generatePublishedPath(IFile file) {
		// IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();
		if ((path != null) && (path.segmentCount() > 1)) {
			path = path.removeFirstSegments(1);
			// return IPath.SEPARATOR + project.getName() + IPath.SEPARATOR + path.toString();
			// #177
			return IPath.SEPARATOR + path.toString();
		}
		return null;
	}

	@Override
	public String getPublishedLocation(IFile file) {
		return CommonIDEParameters.getServicesUrl() + IPath.SEPARATOR + ICommonConstants.REGISTRY + IPath.SEPARATOR + getFolderType()
				+ generatePublishedPath(file);
	}

	@Override
	public String getActivatedLocation(IFile file) {
		return CommonIDEParameters.getServicesUrl() + IPath.SEPARATOR + ICommonConstants.SANDBOX + IPath.SEPARATOR + getFolderType()
				+ generatePublishedPath(file);
	}

	@Override
	public String getPublishedEndpoint(IFile file) {
		if (getPublishedContainerMapping(file) == null) {
			// no real container at runtime
			return null;
		}
		return CommonIDEParameters.getServicesUrl() + getPublishedContainerMapping(file) + generatePublishedPath(file);
	}

	@Override
	public String getActivatedEndpoint(IFile file) {
		if (getActivatedContainerMapping(file) == null) {
			// no real container at runtime
			return null;
		}
		return (IRepository.SEPARATOR.equals(CommonIDEParameters.getServicesUrl()) ? "" : CommonIDEParameters.getServicesUrl())
				+ getActivatedContainerMapping(file) + generatePublishedPath(file);
	}

	protected boolean checkFolderType(IFile file) {
		IPath path = file.getProjectRelativePath();
		if ((path != null) && (path.segmentCount() > 0)) {
			String folderTypeSegment = path.segment(0);
			if (getFolderType().equals(folderTypeSegment)) {
				return true;
			}
		}
		return false;
	}

	protected abstract String getSandboxLocation();

	protected abstract String getRegistryLocation();

	@Override
	public void activateFile(IFile file) throws PublishException {
		if (!recognizedFile(file)) {
			return;
		}
		try {
			final org.eclipse.dirigible.repository.api.IResource targetFile = getTargetFileLocation(file, getSandboxLocation());
			if (file.exists()) {
				copyFileInto(file, targetFile.getParent(), getUser());
			} else {
				if (targetFile.exists()) {
					targetFile.delete();
				}
			}
		} catch (Exception ex) {
			throw new PublishException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getDebugEndpoint(IFile file) {
		return null;
	}

	/**
	 * Utility method for getting the Workspace
	 *
	 * @return
	 */
	public String getWorkspaceLocation() {
		return CommonIDEParameters.getWorkspace();
	}

	/**
	 * Retrieve the project's repository representation as {@link ICollection}
	 *
	 * @param project
	 * @return
	 */
	public ICollection getSourceProjectContainer(IProject project) {
		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection workspaceContainer = repository.getCollection(getWorkspaceLocation());
		final ICollection projectContainer = workspaceContainer.getCollection(project.getName());
		return projectContainer;
	}

}
