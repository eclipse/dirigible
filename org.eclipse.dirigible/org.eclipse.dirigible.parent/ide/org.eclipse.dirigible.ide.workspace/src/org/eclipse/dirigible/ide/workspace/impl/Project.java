/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.dirigible.ide.workspace.impl.event.ResourceChangeEvent;
import org.eclipse.dirigible.repository.logging.Logger;

@SuppressWarnings("deprecation")
public class Project extends Container implements IProject {

	private static final String METHOD_NOT_SUPPORTED = Messages.Project_METHOD_NOT_SUPPORTED;
	private static final String COULD_NOT_CREATE_PROJECT = Messages.Project_COULD_NOT_CREATE_PROJECT;
	private static final String PROJECT_ALREADY_EXIST = Messages.Project_PROJECT_ALREADY_EXIST;

	private static final Logger logger = Logger.getLogger(Project.class);

	/**
	 * Use some persistence method.
	 */
	private boolean opened = true; // FIXME: false
	private IProjectDescription description;

	public Project(IPath path, Workspace workspace) {
		super(path, workspace);
		if (this.description == null) {
			this.description = createDescriptor(this);
		}
	}

	@Override
	public void build(int arg0, IProgressMonitor arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void build(int arg0, String arg1, Map arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close(IProgressMonitor arg0) throws CoreException {
		opened = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(IProgressMonitor monitor) throws CoreException {
		monitor = Resource.monitorWrapper(monitor);
		try {
			if (monitor != null) {
				monitor.beginTask("project creation", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			}
			IStatus pathValidation = workspace.validatePath(path.toString(), PROJECT);
			if (!pathValidation.isOK()) {
				throw new CoreException(pathValidation);
			}
			if (exists()) {
				throw new CoreException(createErrorStatus(PROJECT_ALREADY_EXIST));
			}
			try {
				getEntity().create();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw new CoreException(createErrorStatus(String.format(COULD_NOT_CREATE_PROJECT, ex.getMessage()))); // NOPMD
			}
			workspace.notifyResourceChanged(new ResourceChangeEvent(this, ResourceChangeEvent.POST_CHANGE));

			if (this.description == null) {
				this.description = createDescriptor(this);
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	private IProjectDescription createDescriptor(final Project project) {
		IProjectDescription projectDescription = new IProjectDescription() {

			@Override
			public void setReferencedProjects(IProject[] projects) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setNatureIds(String[] natures) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setName(String projectName) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setLocationURI(URI location) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setLocation(IPath location) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setDynamicReferences(IProject[] projects) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setComment(String comment) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setBuildSpec(ICommand[] buildSpec) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setBuildConfigs(String[] configNames) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setBuildConfigReferences(String configName, IBuildConfiguration[] references) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setActiveBuildConfig(String configName) {
				// TODO Auto-generated method stub

			}

			@Override
			public ICommand newCommand() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasNature(String natureId) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public IProject[] getReferencedProjects() {
				// TODO Auto-generated method stub
				return new IProject[] {};
			}

			@Override
			public String[] getNatureIds() {
				// TODO Auto-generated method stub
				return new String[] {};
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				// return null;
				return project.getName();
			}

			@Override
			public URI getLocationURI() {
				// TODO Auto-generated method stub
				// return null;
				return project.getLocationURI();
			}

			@Override
			public IPath getLocation() {
				// TODO Auto-generated method stub
				// return null;
				return project.getLocation();
			}

			@Override
			public IProject[] getDynamicReferences() {
				// TODO Auto-generated method stub
				// return null;
				return new IProject[] {};
			}

			@Override
			public String getComment() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ICommand[] getBuildSpec() {
				// TODO Auto-generated method stub
				// return null;
				return new ICommand[] {};
			}

			@Override
			public IBuildConfiguration[] getBuildConfigReferences(String configName) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return projectDescription;
	}

	@Override
	public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
		// throw new UnsupportedOperationException(METHOD_NOT_SUPPORTED);

		this.description = description;
		create(monitor);
	}

	@Override
	public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(METHOD_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {
		int flags = (deleteContent ? IResource.ALWAYS_DELETE_PROJECT_CONTENT : IResource.NEVER_DELETE_PROJECT_CONTENT)
				| (force ? FORCE : IResource.NONE);
		delete(flags, monitor);
	}

	@Override
	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		workspace.notifyResourceChanged(new ResourceChangeEvent(this, ResourceChangeEvent.PRE_DELETE));
		super.delete(updateFlags, monitor);
	}

	@Override
	public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
		// return new IContentTypeMatcher() {
		//
		// @Override
		// public IContentDescription getDescriptionFor(Reader contents,
		// String fileName, QualifiedName[] options) throws IOException {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public IContentDescription getDescriptionFor(InputStream contents,
		// String fileName, QualifiedName[] options) throws IOException {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public IContentType[] findContentTypesFor(InputStream contents,
		// String fileName) throws IOException {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public IContentType[] findContentTypesFor(String fileName) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public IContentType findContentTypeFor(InputStream contents, String fileName)
		// throws IOException {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public IContentType findContentTypeFor(String fileName) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// };
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectDescription getDescription() throws CoreException {
		// throw new UnsupportedOperationException();
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFile getFile(String name) {
		IPath resourcePath = this.path.append(name);
		return new File(resourcePath, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFolder getFolder(String name) {
		IPath resourcePath = this.path.append(name);
		return new Folder(resourcePath, workspace);
	}

	@Override
	public IProjectNature getNature(String arg0) throws CoreException {
		// throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public IPathVariableManager getPathVariableManager() {
		// throw new UnsupportedOperationException();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
		return getWorkingLocation(plugin.getUniqueIdentifier());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProject[] getReferencedProjects() throws CoreException {
		return new IProject[0]; // This project never references projects
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProject[] getReferencingProjects() {
		return new IProject[0]; // This project is never referenced
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPath getWorkingLocation(String id) {
		// We do not support working area
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNature(String arg0) throws CoreException {
		// throw new UnsupportedOperationException();
		return false;
	}

	@Override
	public boolean isNatureEnabled(String arg0) throws CoreException {
		// throw new UnsupportedOperationException();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpen() {
		return opened;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
		final int flags = force ? FORCE : IResource.NONE;
		move(description, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open(IProgressMonitor monitor) throws CoreException {
		open(IResource.NONE, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open(int updateFlags, IProgressMonitor monitor) throws CoreException {
		opened = true;
	}

	@Override
	public void setDescription(IProjectDescription projectDescription, IProgressMonitor arg1) throws CoreException {
		// throw new UnsupportedOperationException();
		this.description = projectDescription;
	}

	@Override
	public void setDescription(IProjectDescription projectDescription, int arg1, IProgressMonitor arg2) throws CoreException {
		// throw new UnsupportedOperationException();
		this.description = projectDescription;
	}

	@Override
	public void loadSnapshot(int arg0, URI arg1, IProgressMonitor arg2) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveSnapshot(int arg0, URI arg1, IProgressMonitor arg2) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IBuildConfiguration getActiveBuildConfig() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBuildConfiguration getBuildConfig(String configName) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBuildConfiguration[] getBuildConfigs() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBuildConfiguration[] getReferencedBuildConfigs(String configName, boolean includeMissing) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasBuildConfig(String configName) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearCachedDynamicReferences() {
		// TODO Auto-generated method stub
	}

}
