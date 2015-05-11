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

package org.eclipse.dirigible.ide.workspace.ui.viewer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.ui.viewer.messages"; //$NON-NLS-1$
	public static String WorkspaceContentProvider_COULD_NOT_DETERMINE_IF_CONTAINER_HAS_CHILDREN;
	public static String WorkspaceContentProvider_COULD_NOT_GET_THE_CONTAINER_S_CHILDREN;
	public static String WorkspaceDragSourceListener_WE_HAVE_AN_UNKNOWN_ELEMENT_IN_THE_SELECTION;
	public static String WorkspaceDropTargetListener_COULD_NOT_MOVE_RESOURCE;
	public static String WorkspaceDropTargetListener_NULL_TRANSFER_DATA;
	public static String WorkspaceDropTargetListener_TRYING_TO_MOVE_A_RESOURCE_TO_ITSELF;
	public static String WorkspaceDropTargetListener_UNKNOWN_DROP_TARGET;
	public static String WorkspaceTransfer_COULD_NOT_PERSIST_TRANSFER_DATA;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
