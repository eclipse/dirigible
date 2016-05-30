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

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.ExtensionPointUtils;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Creates and manages all the registered {@link IPublisher} objects.
 * Global 'Activate' and 'Publish' processes are performed by PublishManager
 */
public final class PublishManager {

	private static final Logger logger = Logger.getLogger(PublishManager.class);

	private static final String PUBLISH_ERROR = Messages.getString("PublishManager.PUBLISH_ERROR"); //$NON-NLS-1$

	private static final String THE_USER_S_DOES_NOT_HAVE_OPERATOR_ROLE_TO_PERFORM_PUBLISH_OPERATION = Messages
			.getString("PublishManager.THE_USER_S_DOES_NOT_HAVE_OPERATOR_ROLE_TO_PERFORM_PUBLISH_OPERATION"); //$NON-NLS-1$

	private static final String PUBLISHER_EXTENSION_HAS_AN_INVALID_IMPLEMENTING_CLASS_CONFIGURED = Messages
			.getString("PublishManager.PUBLISHER_EXTENSION_HAS_AN_INVALID_IMPLEMENTING_CLASS_CONFIGURED"); //$NON-NLS-1$

	private static final String COULD_NOT_CREATE_PUBLISHER_INSTANCE = Messages.getString("PublishManager.COULD_NOT_CREATE_PUBLISHER_INSTANCE"); //$NON-NLS-1$

	private static final String EXTENSION_POINT_0_COULD_NOT_BE_FOUND = Messages.getString("PublishManager.EXTENSION_POINT_0_COULD_NOT_BE_FOUND"); //$NON-NLS-1$

	private static final String PUBLISHER_EXTENSION_POINT_ID = "org.eclipse.dirigible.ide.publish.publisher"; //$NON-NLS-1$

	private static final String PUBLISHER_ELEMENT_NAME = "publisher"; //$NON-NLS-1$

	private static final String PUBLISHER_CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private static final String UNKNOWN_SELECTION_TYPE = "Unknown Selection Type"; //$NON-NLS-1$

	static List<IPublisher> publishers = null;

	/**
	 * Returns a list {@link IPublisher}
	 *
	 * @return a list of {@link IPublisher} or <code>null</code> such is not
	 *         found that can handle the specified project type.
	 */
	public static List<IPublisher> getPublishers() {

		synchronized (PublishManager.class) {
			if (publishers == null) {
				publishers = new ArrayList<IPublisher>();
				final IExtensionPoint extensionPoint = ExtensionPointUtils.getExtensionPoint(PUBLISHER_EXTENSION_POINT_ID);
				if (extensionPoint == null) {
					throw new PublishManagerException(format(EXTENSION_POINT_0_COULD_NOT_BE_FOUND, PUBLISHER_EXTENSION_POINT_ID));
				}
				final IConfigurationElement[] publisherElements = getPublisherElements(extensionPoint.getExtensions());

				String publisherName = null;
				try {
					for (IConfigurationElement publisherElement : publisherElements) {
						publisherName = publisherElement.getAttribute(PUBLISHER_CLASS_ATTRIBUTE);
						publishers.add(createPublisher(publisherElement));
					}
				} catch (CoreException ex) {
					throw new PublishManagerException(String.format(COULD_NOT_CREATE_PUBLISHER_INSTANCE, publisherName), ex);
				}
			}
		}
		return publishers;
	}

	public static IProject[] getProjects(ISelection selection) {
		if ((selection == null) || !(selection instanceof IStructuredSelection)) {
			logger.error(UNKNOWN_SELECTION_TYPE);
			return new IProject[0];
		}
		final Set<IProject> result = new HashSet<IProject>();
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		IProject project = null;
		for (Object element : structuredSelection.toArray()) {
			project = getProject(element);
			if (project != null) {
				result.add(project);
			}
		}
		return result.toArray(new IProject[0]);
	}

	public static IFile[] getFiles(ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			logger.error(UNKNOWN_SELECTION_TYPE);
			return new IFile[0];
		}
		final Set<IFile> result = new HashSet<IFile>();
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		for (Object element : structuredSelection.toArray()) {
			if (element instanceof IFile) {
				result.add((IFile) element);
			}
		}
		return result.toArray(new IFile[0]);
	}

	private static IProject getProject(Object element) {
		IProject project = null;
		if (element instanceof IProject) {
			project = (IProject) element;
		} else if (element instanceof IFile) {
			project = ((IFile) element).getProject();
		} else if (element instanceof IFolder) {
			project = ((IFolder) element).getProject();
		}
		return project;
	}

	public static void activateProject(IProject project) throws PublishException {
		publish(project, false);
	}

	public static void publishProject(IProject project) throws PublishException {
		if (!CommonIDEParameters.isUserInRole(IRoles.ROLE_OPERATOR)) {
			String message = String.format(THE_USER_S_DOES_NOT_HAVE_OPERATOR_ROLE_TO_PERFORM_PUBLISH_OPERATION, CommonIDEParameters.getUserName());
			MessageDialog.openError(null, PUBLISH_ERROR, message);
			return;
		}
		publish(project, true);
	}

	private static void publish(IProject project, boolean publish) throws PublishException {
		final List<IPublisher> publishers = PublishManager.getPublishers();

		for (IPublisher iPublisher : publishers) {
			IPublisher publisher = iPublisher;
			if (publish) {
				publisher.publish(project);
			} else {
				publisher.activate(project);
			}
		}
	}

	private static IConfigurationElement[] getPublisherElements(IExtension[] extensions) {
		final List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
		for (IExtension extension : extensions) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				if (PUBLISHER_ELEMENT_NAME.equals(element.getName())) {
					result.add(element);
				}
			}
		}
		return result.toArray(new IConfigurationElement[0]);
	}

	private static IPublisher createPublisher(IConfigurationElement publisherElement) throws CoreException {
		final Object publisher = publisherElement.createExecutableExtension(PUBLISHER_CLASS_ATTRIBUTE);
		if (!(publisher instanceof IPublisher)) {
			throw new PublishManagerException(PUBLISHER_EXTENSION_HAS_AN_INVALID_IMPLEMENTING_CLASS_CONFIGURED);
		}
		return (IPublisher) publisher;
	}

	private PublishManager() {
		super();
	}

}
