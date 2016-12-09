/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.publish.ui.command;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.view.WebViewerView;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class AutoActivator implements // ISaveParticipant {
		IResourceChangeListener {

	private static final String FAILED_TO_ACTIVATE_PROJECT = Messages.AutoActivateAction_FAILED_TO_ACTIVATE_PROJECT;
	private static final String FAILED_TO_ACTIVATE_FILE = Messages.AutoActivateAction_FAILED_TO_ACTIVATE_FILE;
	private static final String FAILED_TO_PUBLISH_PROJECT = Messages.AutoActivateAction_FAILED_TO_PUBLISH_PROJECT;

	private static final String AUTO_ACTIVATION_FAILED = Messages.AutoActivateAction_AUTO_ACTIVATION_FAILED;
	private static final String AUTO_PUBLISH_FAILED = Messages.AutoActivateAction_AUTO_PUBLISH_FAILED;

	private static final Logger logger = Logger.getLogger(AutoActivator.class);

	public void registerListener() {
		WorkspaceLocator.getWorkspace().addResourceChangeListener(this);
	}

	public void unregisterListener() {
		WorkspaceLocator.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		autoActivate(event);
		autoPublish(event);

	}

	private void autoPublish(IResourceChangeEvent event) {
		if (!CommonIDEParameters.isAutoPublishEnabled()) {
			return;
		}
		IResource delta = event.getResource();
		IProject project = delta.getProject();
		try {
			PublishManager.publishProject(project, CommonIDEParameters.getRequest());
		} catch (PublishException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), AUTO_PUBLISH_FAILED,
					FAILED_TO_PUBLISH_PROJECT + project.getName());
		}
	}

	private void autoActivate(IResourceChangeEvent event) {
		if (!CommonIDEParameters.isAutoActivateEnabled()) {
			return;
		}
		IResource delta = event.getResource();
		if ((delta == null) && (event.getDelta() != null)) {
			delta = locateResource(event);
		}
		if (delta != null) {
			if (delta instanceof IFile) {
				activateFile((IFile) delta);
			} else {
				if (delta.getProject() != null) {
					activate(delta.getProject());
				}
			}
		}
	}

	private IResource locateResource(IResourceChangeEvent event) {
		IResource resource = null;
		if (event.getDelta().getAffectedChildren().length > 0) {
			resource = locateResourceFromChild(event.getDelta().getAffectedChildren()[0]);
		}
		return resource;
	}

	private IResource locateResourceFromChild(IResourceDelta resourceDelta) {
		IResource resource = null;
		if (resourceDelta.getAffectedChildren().length > 0) {
			resource = locateResourceFromChild(resourceDelta.getAffectedChildren()[0]);
		} else {
			resource = resourceDelta.getResource();
		}
		return resource;
	}

	private void activate(IProject project) {
		try {
			PublishManager.activateProject(project, CommonIDEParameters.getRequest());
		} catch (PublishException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), AUTO_ACTIVATION_FAILED,
					FAILED_TO_ACTIVATE_PROJECT + project.getName());
		}
		WebViewerView.refreshWebViewerViewIfVisible();
	}

	private void activateFile(IFile file) {
		try {
			final List<IPublisher> publishers = PublishManager.getPublishers();

			for (IPublisher iPublisher : publishers) {
				IPublisher publisher = iPublisher;
				if (publisher.isAutoActivationAllowed()) {
					publisher.activateFile(file, CommonIDEParameters.getRequest());
				}
			}
		} catch (PublishException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), AUTO_ACTIVATION_FAILED,
					FAILED_TO_ACTIVATE_FILE + file.getName());
		}
		WebViewerView.refreshWebViewerViewIfVisible();
	}

}
