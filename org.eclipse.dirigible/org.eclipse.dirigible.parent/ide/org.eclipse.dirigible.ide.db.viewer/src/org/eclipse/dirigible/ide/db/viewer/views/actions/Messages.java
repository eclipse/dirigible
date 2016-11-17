/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.db.viewer.views.actions.messages"; //$NON-NLS-1$
	public static String DeleteTableAction_DATABASE_VIEW;
	public static String DeleteTableAction_DELETE_TABLE;
	public static String DeleteTableAction_DELETE_VIEW;
	public static String DeleteTableAction_FAILED_TO_DELETE_TABLE_S;
	public static String DeleteTableAction_WARNING_THIS_ACTION_WILL_DELETE_THE_TABLE_AND_ALL_OF_ITS_CONTENT_CONTINUE;
	public static String DeleteTableAction_WARNING_THIS_ACTION_WILL_DELETE_THE_VIEW_CONTINUE;
	public static String DeleteTableAction_WILL_DELETE_THE_TABLE_AND_ITS_CONTENT;
	public static String DeleteTableAction_WILL_DELETE_THE_VIEW;
	public static String RefreshViewAction_REFRESH;
	public static String RefreshViewAction_REFRESH_DATABASE_BROWSER;
	public static String ShowTableDefinitionAction_OPEN_TABLE_DEFINITION;
	public static String ShowTableDefinitionAction_WILL_SHOW_TABLE_DEFINITION_CONTENT;
	public static String ViewTableContentAction_CANNOT_OPEN_SQL_VIEW;
	public static String ViewTableContentAction_DATABASE_VIEW;
	public static String ViewTableContentAction_SHOW_CONTENT;
	public static String ViewTableContentAction_WILL_SHOW_TABLE_CONTENT;

	public static String ExportDataAction_EXPORT_DATA;
	public static String ExportDataAction_EXPORT_DATA_AS_DSV_FILE;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
