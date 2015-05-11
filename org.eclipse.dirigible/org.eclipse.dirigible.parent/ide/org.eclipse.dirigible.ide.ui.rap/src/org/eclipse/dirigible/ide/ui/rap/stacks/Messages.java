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

package org.eclipse.dirigible.ide.ui.rap.stacks;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.ui.rap.stacks.messages"; //$NON-NLS-1$
	public static String ConfigurationDialog_CANCEL;
	public static String ConfigurationDialog_CONFIGURATION_FOR;
	public static String ConfigurationDialog_OK;
	public static String ViewStackPresentation_CONFIGURE_THE_ACTIONS_AND_VIEWMENU_FROM;
	public static String ViewStackPresentation_HAS_NO_ACTIONS_OR_VIEWMENU_TO_CONFIGURE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
