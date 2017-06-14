/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * This class defines internal constants for images that intended to be
 * available only within the IDE UI proper. Use
 * {@link IDEInternalWorkbenchImages#getImageDescriptor} to retrieve image
 * descriptors for any of the images named in this class.
 * <b>Note:</b> The existence of these images is not made publically available
 * because such images are subject to change as the dialogs evolve through
 * successive releases.
 */
public final class IDEInternalWorkbenchImages {

	/** Block instantiation. */
	private IDEInternalWorkbenchImages() {
	}

	/*** Constants for Images ***/

	// other toolbar buttons
	public final static String IMG_ETOOL_BUILD_EXEC = "IMG_ETOOL_BUILD_EXEC"; //$NON-NLS-1$
	public final static String IMG_ETOOL_BUILD_EXEC_HOVER = "IMG_ETOOL_BUILD_EXEC_HOVER"; //$NON-NLS-1$
	public final static String IMG_ETOOL_BUILD_EXEC_DISABLED = "IMG_ETOOL_BUILD_EXEC_DISABLED"; //$NON-NLS-1$
	public final static String IMG_ETOOL_SEARCH_SRC = "IMG_ETOOL_SEARCH_SRC"; //$NON-NLS-1$
	public final static String IMG_ETOOL_SEARCH_SRC_HOVER = "IMG_ETOOL_SEARCH_SRC_HOVER"; //$NON-NLS-1$
	public final static String IMG_ETOOL_SEARCH_SRC_DISABLED = "IMG_ETOOL_SEARCH_SRC_DISABLED"; //$NON-NLS-1$
	public final static String IMG_ETOOL_NEXT_NAV = "IMG_ETOOL_NEXT_NAV"; //$NON-NLS-1$
	public final static String IMG_ETOOL_PREVIOUS_NAV = "IMG_ETOOL_PREVIOUS_NAV"; //$NON-NLS-1$
	public final static String IMG_ETOOL_PROBLEM_CATEGORY = "IMG_ETOOL_PROBLEM_CATEGORY"; //$NON-NLS-1$
	public final static String IMG_LCL_FLAT_LAYOUT = "IMG_LCL_FLAT_LAYOUT"; //$NON-NLS-1$
	public final static String IMG_LCL_HIERARCHICAL_LAYOUT = "IMG_LCL_HIERARCHICAL_LAYOUT"; //$NON-NLS-1$

	// wizard images
	public final static String IMG_WIZBAN_NEWPRJ_WIZ = "IMG_WIZBAN_NEWPRJ_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_NEWFOLDER_WIZ = "IMG_WIZBAN_NEWFOLDER_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_NEWFILE_WIZ = "IMG_WIZBAN_NEWFILE_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_IMPORTDIR_WIZ = "IMG_WIZBAN_IMPORTDIR_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_IMPORTZIP_WIZ = "IMG_WIZBAN_IMPORTZIP_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_EXPORTDIR_WIZ = "IMG_WIZBAN_EXPORTDIR_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_EXPORTZIP_WIZ = "IMG_WIZBAN_EXPORTZIP_WIZ"; //$NON-NLS-1$
	public final static String IMG_WIZBAN_RESOURCEWORKINGSET_WIZ = "IMG_WIZBAN_EXPORTZIP_WIZ"; //$NON-NLS-1$
	public final static String IMG_DLGBAN_SAVEAS_DLG = "IMG_DLGBAN_SAVEAS_DLG"; //$NON-NLS-1$
	public final static String IMG_DLGBAN_QUICKFIX_DLG = "IMG_DLGBAN_QUICKFIX_DLG"; //$NON-NLS-1$

	// task objects
	public final static String IMG_OBJS_COMPLETE_TSK = "IMG_OBJS_COMPLETE_TSK"; //$NON-NLS-1$
	public final static String IMG_OBJS_INCOMPLETE_TSK = "IMG_OBJS_INCOMPLETE_TSK"; //$NON-NLS-1$

	// problems images
	public static final String IMG_OBJS_ERROR_PATH = "IMG_OBJS_ERROR_PATH"; //$NON-NLS-1$
	public static final String IMG_OBJS_WARNING_PATH = "IMG_OBJS_WARNING_PATH"; //$NON-NLS-1$
	public static final String IMG_OBJS_INFO_PATH = "IMG_OBJS_INFO_PATH"; //$NON-NLS-1$

	// product
	public final static String IMG_OBJS_DEFAULT_PROD = "IMG_OBJS_DEFAULT_PROD"; //$NON-NLS-1$

	// welcome
	public final static String IMG_OBJS_WELCOME_ITEM = "IMG_OBJS_WELCOME_ITEM"; //$NON-NLS-1$
	public final static String IMG_OBJS_WELCOME_BANNER = "IMG_OBJS_WELCOME_BANNER"; //$NON-NLS-1$

	// Quick fix images
	public static final String IMG_DLCL_QUICK_FIX_DISABLED = "IMG_DLCL_QUICK_FIX_DISABLED";//$NON-NLS-1$
	public static final String IMG_ELCL_QUICK_FIX_ENABLED = "IMG_ELCL_QUICK_FIX_ENABLED"; //$NON-NLS-1$

	/**
	 * Returns the image descriptor for the workbench image with the given
	 * symbolic name. Use this method to retrieve image descriptors for any of
	 * the images named in this class.
	 *
	 * @param symbolicName
	 *            the symbolic name of the image
	 * @return the image descriptor, or <code>null</code> if none
	 */
	public static ImageDescriptor getImageDescriptor(String symbolicName) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(symbolicName);
	}
}
