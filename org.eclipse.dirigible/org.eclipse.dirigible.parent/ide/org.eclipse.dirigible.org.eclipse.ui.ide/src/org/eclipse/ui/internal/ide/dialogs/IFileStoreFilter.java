/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.ide.dialogs;

import org.eclipse.core.filesystem.IFileStore;

/**
 * IFileStoreFilter is an interface that defines a filter on file stores.
 * 
 * @since 3.2
 * 
 */
public interface IFileStoreFilter {

	/**
	 * Return whether or not this store is accepted by the receiver.
	 * 
	 * @param store
	 *            IFileStore
	 * @return boolean <code>true</code> if this store is accepted.
	 */
	public abstract boolean accept(IFileStore store);

}
