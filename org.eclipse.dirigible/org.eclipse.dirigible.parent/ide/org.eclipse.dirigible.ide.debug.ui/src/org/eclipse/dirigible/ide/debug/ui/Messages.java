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

package org.eclipse.dirigible.ide.debug.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.debug.ui.messages"; //$NON-NLS-1$
	public static String DebugView_CONTINUE;
	public static String DebugView_FILE;
	public static String DebugView_REFRESH;
	public static String DebugView_ROW;
	public static String DebugView_SKIP_BREAKPOINTS;
	public static String DebugView_SOURCE;
	public static String DebugView_STEP_INTO;
	public static String DebugView_STEP_OVER;
	public static String DebugView_VALUES;
	public static String DebugView_VARIABLES;
	public static String DebugView_SESSIONS;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
