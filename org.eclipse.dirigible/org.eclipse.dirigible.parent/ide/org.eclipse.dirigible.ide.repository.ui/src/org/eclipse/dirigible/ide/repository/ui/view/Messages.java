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

package org.eclipse.dirigible.ide.repository.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.repository.ui.view.messages"; //$NON-NLS-1$
	public static String RepositoryView_CHECK_LOGS_FOR_MORE_INFO;
	public static String RepositoryView_COULD_NOT_ACCESS_REPOSITORY;
	public static String RepositoryView_COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;
	public static String RepositoryView_OPERATION_FAILED;
	public static String ResourceHistoryView_CREATED_AT;
	public static String ResourceHistoryView_CREATED_BY;
	public static String ResourceHistoryView_RESOURCE_HISTORY;
	public static String ResourceHistoryView_VERSION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
