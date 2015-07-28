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

package org.eclipse.dirigible.ide.db.export;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.db.export.messages"; //$NON-NLS-1$
	public static String DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV;
	public static String DataExportDialog_AVAILABLE_TABLES_AND_VIEWS;
	public static String DataExportDialog_ERROR_ON_LOADING_TABLES_FROM_DATABASE_FOR_GENERATION;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
