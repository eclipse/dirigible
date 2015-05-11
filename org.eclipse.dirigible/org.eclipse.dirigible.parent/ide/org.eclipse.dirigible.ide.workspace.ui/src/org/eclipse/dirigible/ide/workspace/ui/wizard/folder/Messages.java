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

package org.eclipse.dirigible.ide.workspace.ui.wizard.folder;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.ui.wizard.folder.messages"; //$NON-NLS-1$
	public static String NewFolderWizard_COULD_NOT_CREATE_FOLDER;
	public static String NewFolderWizard_FOLDER_S_CREATED_SUCCESSFULLY;
	public static String NewFolderWizard_OPERATION_FAILED;
	public static String NewFolderWizard_WINDOW_TITLE;
	public static String NewFolderWizardMainPage_FOLDER_NAME;
	public static String NewFolderWizardMainPage_PAGE_DESCRIPTION;
	public static String NewFolderWizardMainPage_PAGE_TITLE;
	public static String NewFolderWizardMainPage_PARENT_LOCATION;
	public static String NewFolderWizardModel_A_RESOURCE_WITH_THAT_PATH_ALREADY_EXISTS;
	public static String NewFolderWizardModel_FOLDER_NAME_CANNOT_BE_NULL;
	public static String NewFolderWizardModel_INVALID_FOLDER_NAME;
	public static String NewFolderWizardModel_INVALID_PARENT_LOCATION;
	public static String NewFolderWizardModel_PARENT_LOCATION_CANNOT_BE_NULL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
