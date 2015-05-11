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

package org.eclipse.dirigible.ide.repository.ui.command;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.repository.ui.command.messages"; //$NON-NLS-1$
	public static String DeleteHandler_ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEM;
	public static String DeleteHandler_ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEMS_D;
	public static String DeleteHandler_CONFIRM_DELETE;
	public static String DeleteHandler_DELETE_ERROR;
	public static String DeleteHandler_SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_DELETED;
	public static String OpenHandler_COULD_NOT_OPEN_ONE_OR_MORE_FILES;
	public static String OpenHandler_OPEN_FAILURE;
	public static String PasteHandler_PASTE_ERROR;
	public static String PasteHandler_SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_PASTED;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
