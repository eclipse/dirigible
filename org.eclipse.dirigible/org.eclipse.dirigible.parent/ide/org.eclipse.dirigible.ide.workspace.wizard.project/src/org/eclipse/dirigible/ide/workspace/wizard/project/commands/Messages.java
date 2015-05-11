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

package org.eclipse.dirigible.ide.workspace.wizard.project.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.wizard.project.commands.messages"; //$NON-NLS-1$
	public static String UploadProjectHandler_CANNOT_CLOSE_INPUT_STREAM_TO_AN_UPLOADED_FILE;
	public static String UploadProjectHandler_CANNOT_SAVE_UPLOADED_FILE;
	public static String UploadProjectHandler_CANNOT_UPLOAD;
	public static String UploadProjectHandler_OVERRIDE_PROJECTS;
	public static String UploadProjectHandler_REASON;
	public static String UploadProjectHandler_THIS_PROCESS_WILL_OVERRIDE_YOUR_EXISTING_PROJECTS_DO_YOU_WANT_TO_CONTINUE;
	public static String UploadProjectHandler_UPLOAD_ERROR;
	public static String UploadProjectHandler_UPLOAD_PROJECT_ARCHIVE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
