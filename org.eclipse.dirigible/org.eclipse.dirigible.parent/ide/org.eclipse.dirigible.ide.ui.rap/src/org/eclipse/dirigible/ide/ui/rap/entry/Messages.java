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

package org.eclipse.dirigible.ide.ui.rap.entry;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.ui.rap.entry.messages"; //$NON-NLS-1$
	public static String DirigibleActionBarAdvisor_ABOUT;
	public static String DirigibleActionBarAdvisor_COULD_NOT_OPEN_WEB_PAGE;
	public static String DirigibleActionBarAdvisor_FILE;
	public static String DirigibleActionBarAdvisor_HELP;
	public static String DirigibleActionBarAdvisor_RUNNING_ON_RAP_VERSION;
	public static String DirigibleActionBarAdvisor_DIRIGIBLE_HOME;
	public static String DirigibleActionBarAdvisor_DIRIGIBLE_HELP;
	public static String DirigibleActionBarAdvisor_DIRIGIBLE_SAMPLES;
	public static String DirigibleActionBarAdvisor_DIRIGIBLE_FORUM;
	public static String DirigibleActionBarAdvisor_DIRIGIBLE_BUG;
	public static String DirigibleActionBarAdvisor_SHOW_PERSPECTIVE;
	public static String DirigibleActionBarAdvisor_SHOW_VIEW;
	public static String DirigibleActionBarAdvisor_WEB_PAGE_ERROR;
	public static String DirigibleActionBarAdvisor_WINDOW;
	public static String DirigibleActionBarAdvisor_WORKBENCH;
	public static String DirigibleWorkbench_ARE_YOU_SURE_YOU_WANT_TO_QUIT;
	public static String DirigibleWorkbenchWindowAdvisor_WORKBENCH;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
