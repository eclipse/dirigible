/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui;

import org.eclipse.core.resources.IFile;

/**
 * This interface defines a file-oriented input to an editor.
 * <p>
 * Clients implementing this editor input interface should override
 * <code>Object.equals(Object)</code> to answer true for two inputs that are the
 * same. The <code>IWorbenchPage.openEditor</code> APIs are dependent on this to
 * find an editor with the same input.
 * </p>
 * <p>
 * File-oriented editors should support this as a valid input type, and allow
 * full read-write editing of its content.
 * </p>
 * <p>
 * A default implementation of this interface is provided by
 * org.eclipse.ui.part.FileEditorInput.
 * </p>
 * <p>
 * All editor inputs must implement the <code>IAdaptable</code> interface;
 * extensions are managed by the platform's adapter manager.
 * </p>
 * 
 * @see org.eclipse.core.resources.IFile
 */
public interface IFileEditorInput extends IStorageEditorInput {
	/**
	 * Returns the file resource underlying this editor input.
	 * <p>
	 * The <code>IFile</code> returned can be a handle to a resource that does
	 * not exist in the workspace. As such, an editor should provide appropriate
	 * feedback to the user instead of simply failing during input validation.
	 * For example, a text editor could open in read-only mode with a message in
	 * the text area to inform the user that the file does not exist.
	 * </p>
	 * 
	 * @return the underlying file
	 */
	public IFile getFile();
}
