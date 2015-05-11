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

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;

/**
 * Interface for a <code>IStorage</code> input to an editor.
 * <p>
 * Clients implementing this editor input interface should override
 * <code>Object.equals(Object)</code> to answer true for two inputs that are the
 * same. The <code>IWorbenchPage.openEditor</code> APIs are dependent on this to
 * find an editor with the same input.
 * </p>
 * <p>
 * Clients should implement this interface to declare new types of
 * <code>IStorage</code> editor inputs.
 * </p>
 * <p>
 * File-oriented editors should support this as a valid input type, and display
 * its content for viewing (but not allow modification). Within the editor, the
 * "save" and "save as" operations should create a new file resource within the
 * workspace.
 * </p>
 * <p>
 * All editor inputs must implement the <code>IAdaptable</code> interface;
 * extensions are managed by the platform's adapter manager.
 * </p>
 */
public interface IStorageEditorInput extends IEditorInput {
	/**
	 * Returns the underlying IStorage object.
	 * 
	 * @return an IStorage object.
	 * @exception CoreException
	 *                if this method fails
	 */
	public IStorage getStorage() throws CoreException;
}
