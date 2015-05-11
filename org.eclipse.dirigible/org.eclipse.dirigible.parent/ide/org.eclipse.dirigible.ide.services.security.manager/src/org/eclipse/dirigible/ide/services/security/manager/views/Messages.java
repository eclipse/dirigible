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

package org.eclipse.dirigible.ide.services.security.manager.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.services.security.manager.views.messages"; //$NON-NLS-1$
	public static String SecurityManagerView_ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS;
	public static String SecurityManagerView_LOCATION;
	public static String SecurityManagerView_LOCATION_IS_TOO_LONG;
	public static String SecurityManagerView_PROTECT_A_GIVEN_RELATIVE_URL_TRANSITIVELY;
	public static String SecurityManagerView_PROTECTED_URL;
	public static String SecurityManagerView_REFRESH;
	public static String SecurityManagerView_REFRESH_THE_LIST_OF_PROTECTED_LOCATIONS;
	public static String SecurityManagerView_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS;
	public static String SecurityManagerView_ROLES;
	public static String SecurityManagerView_SECURE_LOCATION;
	public static String SecurityManagerView_SECURITY_ERROR;
	public static String SecurityManagerView_UNSECURE_LOCATION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
