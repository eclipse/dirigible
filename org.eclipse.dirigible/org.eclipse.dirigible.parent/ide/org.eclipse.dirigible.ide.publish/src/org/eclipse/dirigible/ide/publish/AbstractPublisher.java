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
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class AbstractPublisher implements IPublisher {
	
	public static final Logger logger = Logger.getLogger(AbstractPublisher.class);
	
	private String currentPublishLocation;

	protected ICollection getTargetProjectContainer(IProject project, String registryLocation)
			throws IOException {
		
		this.currentPublishLocation = registryLocation;
		
		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection publishContainer = repository.getCollection(registryLocation);
//		final ICollection projectContainer = publishContainer.getCollection(project.getName());
		// #177
		final ICollection projectContainer = publishContainer;
		String user = getUser();
		if (projectContainer.exists()) {
			if (!checkOverridePermissionsForFolder(project.getName(), user, projectContainer)) {
				return projectContainer;
			}
		} else {
			projectContainer.create();
		}
		return projectContainer;
	}
	
	protected org.eclipse.dirigible.repository.api.IResource getTargetFileLocation(IFile file, String registryLocation)
			throws IOException {
		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection publishContainer = repository.getCollection(registryLocation);
//		final ICollection projectContainer = publishContainer.getCollection(file.getProject().getName());
		// #177
		final ICollection projectContainer = publishContainer;
		final org.eclipse.dirigible.repository.api.IResource fileResource = 
				projectContainer.getResource(file.getProjectRelativePath().removeFirstSegments(1).toString());
		return fileResource;
	}

	protected IFolder getSourceFolder(IProject project, String sourceFolderName) {
		return project.getFolder(sourceFolderName);
	}
	
	public void copyAllFromTo(IContainer source, ICollection target) throws CoreException,
			IOException {

		if (!source.exists()) {
			return;
		}

		String user = getUser();

		// #177
//		synchronizeRepositoryWithWorkspace(source, target);

		for (IResource resource : source.members()) {
			if (resource instanceof IFolder) {
				copyFolderInto((IFolder) resource, target, user);
			}
			if (resource instanceof IFile) {
				copyFileInto((IFile) resource, target, user);
			}
		}
	}

//	private void synchronizeRepositoryWithWorkspace(IContainer source, ICollection target)
//			throws IOException, CoreException {
//		for (IEntity repositoryResource : target.getChildren()) {
//			if (repositoryResource.exists()) {
//				boolean shouldDelete = true;
//				String repositoryMemberPath = repositoryResource.getName();
//				for (IResource member : source.members()) {
//					String sourceMemberPath = member.getName();
//
//					if (repositoryMemberPath.equals(sourceMemberPath)) {
//						shouldDelete = false;
//						break;
//					}
//				}
//				if (shouldDelete) {
//					repositoryResource.delete();
//				}
//			}
//		}
//	}

	public String getUser() {
		String user = CommonParameters.getUserName();
		return user;
	}

	public void copyFolderInto(IFolder folder, ICollection target, String user)
			throws IOException, CoreException {
		final ICollection targetFolder = target.getCollection(folder.getName());
		if (!checkOverridePermissionsForFolder(folder.getName(), user, targetFolder)) {
			return;
		}

		if (!targetFolder.exists()) {
			targetFolder.create();
		}
		copyAllFromTo(folder, targetFolder);
	}

	private boolean checkOverridePermissionsForFolder(String folderName, String user,
			final ICollection targetFolder) throws IOException {
//		if (targetFolder.exists() && targetFolder.getInformation().getModifiedBy() != null
//				&& !"".equals(targetFolder.getInformation().getModifiedBy()) //$NON-NLS-1$
//				&& !"null".equalsIgnoreCase(targetFolder.getInformation().getModifiedBy()) //$NON-NLS-1$
//				&& !targetFolder.getInformation().getModifiedBy().equalsIgnoreCase(user)) {
//
//			boolean publishOverride = Boolean.parseBoolean(CommonParameters.get(PUBLISH_OVERRIDE));
//			
//			if (!publishOverride) {
//				boolean override = MessageDialog
//						.openConfirm(
//								null,
//								PUBLISH,
//								String.format(
//										FOLDER_WITH_THE_SAME_NAME_ALREADY_PUBLISHED_BY_ANOTHER_USER_S_DO_YOU_WANT_TO_OVERRIDE_IT_ANYWAY,
//										targetFolder.getName(), targetFolder.getInformation().getModifiedBy()));
//				if (!override) {
//					logger.debug(String.format(PUBLISH_OF_FOLDER_S_SKIPPED, folderName));
//					return false;
//				} else {
//					CommonParameters.set(PUBLISH_OVERRIDE, Boolean.TRUE.toString());
//				}
//			}
//			logger.warn(String.format(
//					PUBLISH_OF_FOLDER_S_BY_S_OVERRIDES_THE_PREVIOUS_MODIFICATIONS_MADE_BY_S,
//					folderName, user, targetFolder.getInformation().getModifiedBy()));
//		}
//		return true;
		// #177
		return CommonParameters.isUserInRole(IRoles.ROLE_OPERATOR);
	}

	public void copyFileInto(IFile file, ICollection target, String user) throws IOException,
			CoreException {
		
		String fileLocation = file.getFullPath().toString();
		String projectLocation = file.getProject().getFullPath().toString();
		
		final org.eclipse.dirigible.repository.api.IResource targetResource = 
				target.getRepository().getResource(this.currentPublishLocation + IRepository.SEPARATOR + fileLocation.substring(
						projectLocation.length() + getFolderType().length() + 2));
		if (!checkOverridePermissionsForResource(file.getName(), user, targetResource)) {
			return;
		}

		org.eclipse.dirigible.repository.api.IResource resource = target.getRepository().getResource(
				file.getWorkspace().getRoot().getRawLocation() + file.getFullPath().toString());

		if (targetResource.exists()) {
			Date targetResourceLastModifiedAt = targetResource.getInformation().getModifiedAt();
			Date resourceLastModifiedAt = resource.getInformation().getModifiedAt();

			if (resourceLastModifiedAt.getTime() >= targetResourceLastModifiedAt.getTime()) {
				setTargetResourceContent(file, targetResource, resource);
			}

		} else {
			setTargetResourceContent(file, targetResource, resource);
		}

	}

	private void setTargetResourceContent(IFile file,
			final org.eclipse.dirigible.repository.api.IResource targetResource,
			org.eclipse.dirigible.repository.api.IResource resource) throws IOException, CoreException {
			targetResource.setContent(readFile(file), resource.isBinary(), resource.getContentType());
	}

	private boolean checkOverridePermissionsForResource(String fileName, String user,
			final org.eclipse.dirigible.repository.api.IResource targetResource) throws IOException {
//		if (targetResource.exists() && targetResource.getInformation().getModifiedBy() != null
//				&& !"".equals(targetResource.getInformation().getModifiedBy()) //$NON-NLS-1$
//				&& !"null".equalsIgnoreCase(targetResource.getInformation().getModifiedBy()) //$NON-NLS-1$
//				&& !targetResource.getInformation().getModifiedBy().equalsIgnoreCase(user)) {
//
//			boolean override = MessageDialog
//					.openConfirm(
//							null,
//							PUBLISH,
//							String.format(
//									FILE_WITH_THE_SAME_NAME_ALREADY_PUBLISHED_BY_ANOTHER_USER_S_DO_YOU_WANT_TO_OVERRIDE_IT_ANYWAY,
//									targetResource.getInformation().getModifiedBy()));
//			if (!override) {
//				logger.debug(String.format(PUBLISH_OF_FILE_S_SKIPPED, fileName));
//				return false;
//			}
//
//			logger.warn(String.format(
//					PUBLISH_OF_FILE_S_BY_S_OVERRIDES_THE_PREVIOUS_MODIFICATIONS_MADE_BY_S,
//					fileName, user, targetResource.getInformation().getModifiedBy()));
//		}
//		return true;
		// #177
		return CommonParameters.isUserInRole(IRoles.ROLE_OPERATOR);
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
//		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();
		if (path != null && path.segmentCount() > 1) {
			path = path.removeFirstSegments(1);
//			return IPath.SEPARATOR + project.getName() + IPath.SEPARATOR + path.toString();
//			#177
			return IPath.SEPARATOR + path.toString();
		}
		return null;
	}

	public String getPublishedLocation(IFile file) {
		return CommonParameters.getServicesUrl() + IPath.SEPARATOR + ICommonConstants.REGISTRY
				+ IPath.SEPARATOR + getFolderType() + generatePublishedPath(file);
	}
	
	public String getActivatedLocation(IFile file) {
		return CommonParameters.getServicesUrl() + IPath.SEPARATOR + ICommonConstants.SANDBOX
				+ IPath.SEPARATOR + getFolderType() + generatePublishedPath(file);
	}

	public String getPublishedEndpoint(IFile file) {
		if (getPublishedContainerMapping(file) == null) {
			// no real container at runtime
			return null;
		}
		return CommonParameters.getServicesUrl() + getPublishedContainerMapping(file)
				+ generatePublishedPath(file);
	}
	
	public String getActivatedEndpoint(IFile file) {
		if (getActivatedContainerMapping(file) == null) {
			// no real container at runtime
			return null;
		}
		return (IRepository.SEPARATOR.equals(CommonParameters.getServicesUrl()) ? "" : CommonParameters.getServicesUrl()) 
				+ getActivatedContainerMapping(file)
				+ generatePublishedPath(file);
	}

	protected boolean checkFolderType(IFile file) {
		IPath path = file.getProjectRelativePath();
		if (path != null && path.segmentCount() > 0) {
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
			final org.eclipse.dirigible.repository.api.IResource targetFile = getTargetFileLocation(file,
					getSandboxLocation());
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
	
	public String getWorkspaceLocation() {
		return CommonParameters.getWorkspace();
	}
	
	public ICollection getSourceProjectContainer(IProject project) {
		final IRepository repository = RepositoryFacade.getInstance().getRepository();
		final ICollection workspaceContainer = repository.getCollection(getWorkspaceLocation());
		final ICollection projectContainer = workspaceContainer.getCollection(project.getName());
		return projectContainer;
	}

}
