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

package org.eclipse.dirigible.ide.workspace.ui.wizard.file;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.ui.wizard.file.messages"; //$NON-NLS-1$
	public static String NewFileWizard_COULD_NOT_SAVE_FILE;
	public static String NewFileWizard_FILE_S_CREATED_SUCCESSFULLY;
	public static String NewFileWizard_OPERATION_FAILED;
	public static String NewFileWizard_WINDOW_TITLE;
	public static String NewFileWizardMainPage_FILE_NAME;
	public static String NewFileWizardMainPage_PAGE_DESCRIPTION;
	public static String NewFileWizardMainPage_PAGE_TITLE;
	public static String NewFileWizardMainPage_PARENT_LOCATION;
	public static String NewFileWizardModel_A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS;
	public static String NewFileWizardModel_CONTENT_PROVIDER_CANNOT_BE_NULL;
	public static String NewFileWizardModel_COULD_NOT_READ_FILE_CONTENT;
	public static String NewFileWizardModel_FILE_NAME_CANNOT_BE_NULL;
	public static String NewFileWizardModel_INVALID_FILE_NAME;
	public static String NewFileWizardModel_INVALID_PARENT_PATH;
	public static String NewFileWizardModel_PARENT_LOCATION_CANNOT_BE_NULL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
