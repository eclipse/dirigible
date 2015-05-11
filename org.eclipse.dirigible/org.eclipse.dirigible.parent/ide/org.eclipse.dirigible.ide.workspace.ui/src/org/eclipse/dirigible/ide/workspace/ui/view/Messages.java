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

package org.eclipse.dirigible.ide.workspace.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.ui.view.messages"; //$NON-NLS-1$
	public static String WorkspaceExplorerView_CHECK_LOGS_FOR_MORE_INFO;
	public static String WorkspaceExplorerView_COULD_NOT_EXECUTE_COMMAND;
	public static String WorkspaceExplorerView_COULD_NOT_EXECUTE_OPEN_COMMAND;
	public static String WorkspaceExplorerView_COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;
	public static String WorkspaceExplorerView_COULD_NOT_EXECUTE_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;
	public static String WorkspaceExplorerView_OPERATION_FAILED;
	public static String WorkspaceExplorerView_PUBLISH_FAILED;
	public static String WorkspaceExplorerView_ACTIVATION_FAILED;
	public static String WorkspaceExplorerView_PUBLISH;
	public static String WorkspaceExplorerView_ACTIVATE;
	public static String WorkspaceExplorerView_INVALID_DEFAULT_HANDLER_IMPLEMENTING_CLASS_CONFIGURED;
	public static String WorkspaceExplorerView_COULD_NOT_CREATE_NEW_MENU_ITEM_INSTANCE;
	public static String WorkspaceExplorerView_EXTENSION_POINT_0_COULD_NOT_BE_FOUND;
	public static String WorkspaceExplorerView_NEW;
	public static String WorkspaceExplorerView_SAVE;
	public static String WorkspaceExplorerView_SAVE_ALL;
	public static String WebViewerView_OPEN;
	public static String WebViewerView_PUBLIC;
	public static String WebViewerView_SANDBOX;
	public static String WebViewerView_REFRESH;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
