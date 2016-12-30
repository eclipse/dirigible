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

package org.eclipse.dirigible.ide.db.viewer.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.db.viewer.views.messages"; //$NON-NLS-1$
	public static String AbstractSQLConsole_LIMIT_RESULTS_TO_100_ROWS;
	public static String DatabaseViewer_DATABASE_VIEW;
	public static String SQLConsole_EXECUTE_QUERY;
	public static String SQLConsole_EXECUTE_QUERY_STATEMENT;
	public static String SQLConsole_EXECUTE_UPDATE;
	public static String SQLConsole_EXECUTE_UPDATE_STATEMENT;
	public static String SQLConsole_EXECUTE_UPDATE_TEXT;
	public static String SQLConsole_UPDATE_COUNT_S;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
