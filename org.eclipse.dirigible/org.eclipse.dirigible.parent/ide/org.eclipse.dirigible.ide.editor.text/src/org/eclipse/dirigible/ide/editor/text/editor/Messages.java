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

package org.eclipse.dirigible.ide.editor.text.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.editor.text.editor.messages"; //$NON-NLS-1$
	public static String AbstractTextEditor_CANNOT_SAVE_DOCUMENT;
	public static String AbstractTextEditor_SAVE_ERROR;
	public static String ContentProviderFactory_CANNOT_READ_CONTENT_PROVIDER_EXTENSION_CLASS_0_INPUT_TYPE_1;
	public static String ContentProviderFactory_UNSUPPORTED_I_EDITOR_INPUT;
	public static String DefaultContentProvider_CANNOT_READ_FILE_CONTENTS;
	public static String DefaultContentProvider_CANNOT_SAVE_FILE_CONTENTS;
	public static String DefaultContentProvider_WE_SHOULD_NEVER_GET_HERE;
	public static String TextEditor_CANNOT_LOAD_DOCUMENT;
	public static String TextEditor_EDITOR_INPUT_CANNOT_BE_NULL;
	public static String TextEditor_ERROR;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
