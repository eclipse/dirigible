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

package org.eclipse.dirigible.ide.workspace.ui.commands;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.repository.ui.command.Clipboard;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;

public class PasteHandler extends AbstractClipboardHandler {

	private static final String SELECT_TARGET_FOLDER = Messages.PasteHandler_SELECT_TARGET_FOLDER;
	private static final String SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_PASTED = Messages.PasteHandler_SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_PASTED;
	private static final String PASTE_ERROR = Messages.PasteHandler_PASTE_ERROR;

	protected void execute(ExecutionEvent event, SortedSet<IResource> resources) {
		if (resources.size() == 0) {
			return;
		}

		IRepository repository = RepositoryFacade.getInstance().getRepository();

		IResource targetContainer = resources.first();
		if (targetContainer instanceof IContainer) {
			String targetReposiotryPath = resources.first().getRawLocation()
					.toString();

			Clipboard clipboard = Clipboard.getInstance();

			String command = clipboard.getCommand();

			Throwable throwable = null;
			if (CUT.equals(command) || COPY.equals(command)) {

				for (Iterator<?> iterator = clipboard.iterator(); iterator
						.hasNext();) {
					IResource resource = (IResource) iterator.next();
					String sourceRepositoryPath = resource.getRawLocation()
							.toString();
					try {
						String resourceName = resource.getName();
						ICollection collection = repository
								.getCollection(targetReposiotryPath);
						if (collection.exists()) {

							if (resource instanceof IContainer) {

								String localCollectionName = resourceName;
								int i = 1;
								while (collection.getCollectionsNames()
										.contains(localCollectionName)) {
									localCollectionName = resourceName + i++;
								}
								byte[] data = repository.exportZip(
										sourceRepositoryPath, false);
								repository.importZip(data, targetReposiotryPath
										+ "/" + localCollectionName); //$NON-NLS-1$
							} else if (resource instanceof IFile) {
								String localResourceName = resourceName;
								if (collection.getResourcesNames().contains(
										resourceName)) {
									int i = 1;
									while (collection.getResourcesNames()
											.contains(localResourceName)) {
										localResourceName = resourceName + i++;
									}
								}
								org.eclipse.dirigible.repository.api.IResource sourceResource = repository
										.getResource(sourceRepositoryPath);
								repository.createResource(targetReposiotryPath
										+ "/" + localResourceName, //$NON-NLS-1$
										sourceResource.getContent(), sourceResource.isBinary(), sourceResource.getContentType());
							}
						}
					} catch (IOException e) {
						if (throwable == null) {
							throwable = e;
						}
					}
					if (CUT.equals(command)) {
						try {
							resource.delete(false, null);
						} catch (CoreException e) {
							if (throwable == null) {
								throwable = e;
							}
						}
					}
				}
			}

			if (throwable != null) {
				MessageDialog.openWarning(null, PASTE_ERROR,
						SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_PASTED);
			}

			RefreshHandler.refreshActivePart(event);
		} else {
			MessageDialog.openWarning(null, PASTE_ERROR, SELECT_TARGET_FOLDER);
		}
	}

}
