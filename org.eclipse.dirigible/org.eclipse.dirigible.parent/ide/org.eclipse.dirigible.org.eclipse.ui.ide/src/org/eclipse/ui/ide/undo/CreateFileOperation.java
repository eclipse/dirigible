/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.ide.undo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.internal.ide.undo.ContainerDescription;
import org.eclipse.ui.internal.ide.undo.FileDescription;
import org.eclipse.ui.internal.ide.undo.IFileContentDescription;

/**
 * A CreateFileOperation represents an undoable operation for creating a file in
 * the workspace. If a link location is specified, the folder is considered to
 * be linked to the file at the specified location. If a link location is not
 * specified, the file will be created in the location specified by the handle,
 * and the entire containment path of the file will be created if it does not
 * exist. Clients may call the public API from a background thread.
 * 
 * This class is intended to be instantiated and used by clients. It is not
 * intended to be subclassed by clients.
 * 
 * @since 3.3
 * 
 */
public class CreateFileOperation extends AbstractCreateResourcesOperation {

	/**
	 * Create a CreateFileOperation
	 * 
	 * @param fileHandle
	 *            the file to be created
	 * @param linkLocation
	 *            the location of the file if it is to be linked
	 * @param contents
	 *            the initial contents of the file, or null if there is to be no
	 *            contents
	 * @param label
	 *            the label of the operation
	 */
	public CreateFileOperation(IFile fileHandle, URI linkLocation,
			InputStream contents, String label) {
		super(null, label);
		ResourceDescription resourceDescription;
		if (fileHandle.getParent().exists()) {
			resourceDescription = new FileDescription(fileHandle, linkLocation,
					createFileContentDescription(fileHandle, contents));
		} else {
			// must first ensure descriptions for the parent folders are
			// created
			ContainerDescription containerDescription = ContainerDescription
					.fromContainer(fileHandle.getParent());
			containerDescription.getFirstLeafFolder()
					.addMember(
							new FileDescription(fileHandle, linkLocation,
									createFileContentDescription(fileHandle,
											contents)));
			resourceDescription = containerDescription;
		}
		setResourceDescriptions(new ResourceDescription[] { resourceDescription });

	}

	/*
	 * Create a file state that represents the desired contents and attributes
	 * of the file to be created. Used to mimic file history when a resource is
	 * first created.
	 */
	private IFileContentDescription createFileContentDescription(
			final IFile file, final InputStream contents) {
		return new IFileContentDescription() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ui.internal.ide.undo.IFileContentDescription#getContents
			 * ()
			 */
			public InputStream getContents() {
				if (contents != null) {
					return contents;
				}
				return new ByteArrayInputStream(new byte[0]);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ui.internal.ide.undo.IFileContentDescription#getCharset
			 * ()
			 */
			public String getCharset() {
				try {
					return file.getCharset(false);
				} catch (CoreException e) {
					return null;
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ui.internal.ide.undo.IFileContentDescription#exists()
			 */
			public boolean exists() {
				return true;
			}
		};
	}
}
