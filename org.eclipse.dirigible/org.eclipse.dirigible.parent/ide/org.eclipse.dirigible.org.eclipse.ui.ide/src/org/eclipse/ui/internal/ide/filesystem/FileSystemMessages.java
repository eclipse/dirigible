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

package org.eclipse.ui.internal.ide.filesystem;

import org.eclipse.osgi.util.NLS;

/**
 * FileSystemMessages is the class that handles the messages for the filesystem
 * support.
 * 
 */
public class FileSystemMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.ide.filesystem.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, FileSystemMessages.class);
	}

	/**
	 * The name of the default file system.
	 */
	public static String DefaultFileSystem_name;

	/**
	 * The label for file system selection.
	 */
	public static String FileSystemSelection_title;
}
