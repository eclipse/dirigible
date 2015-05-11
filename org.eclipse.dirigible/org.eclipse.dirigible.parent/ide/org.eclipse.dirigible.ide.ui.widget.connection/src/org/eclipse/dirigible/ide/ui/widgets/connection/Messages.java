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

package org.eclipse.dirigible.ide.ui.widgets.connection;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.ui.widgets.connection.messages"; //$NON-NLS-1$
	public static String ConnectionViewer_COLOR_CANNOT_BE_NULL;
	public static String ConnectionViewer_CONTENT_PROVIDER_MUST_NOT_RETURN_NULL;
	public static String ConnectionViewer_INVALID_OR_MISSING_CONTENT_PROVIDER;
	public static String ConnectionViewer_INVALID_OR_MISSING_LABEL_PROVIDER;
	public static String ConnectionViewer_INVALID_OR_MISSING_SOURCE_ITEM_RESOLVER;
	public static String ConnectionViewer_INVALID_OR_MISSING_TARGET_ITEM_RESOLVER;
	public static String ConnectionViewer_INVALID_OR_NULL_SELECTION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
