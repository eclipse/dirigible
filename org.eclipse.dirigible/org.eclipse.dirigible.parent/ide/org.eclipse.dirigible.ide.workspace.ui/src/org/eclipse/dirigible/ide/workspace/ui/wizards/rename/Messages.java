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

package org.eclipse.dirigible.ide.workspace.ui.wizards.rename;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.ui.wizards.rename.messages"; //$NON-NLS-1$
	public static String RenameWizard_CHECK_LOGS_FOR_MORE_INFO;
	public static String RenameWizard_COULD_NOT_COMPLETE_RESOURCE_RENAME;
	public static String RenameWizard_COULD_NOT_COMPLETE_WIZARD_DUE_TO_THE_FOLLOWING_ERROR;
	public static String RenameWizard_OPERATION_ERROR;
	public static String RenameWizard_RENAME_WIZARD_TITLE;
	public static String RenameWizardModel_A_RESOURCE_WITH_THIS_NAME_ALREADY_EXISTS;
	public static String RenameWizardModel_COULD_NOT_RENAME_RESOURCE;
	public static String RenameWizardModel_INVALID_RESOURCE_NAME;
	public static String RenameWizardNamingPage_ENTER_NEW_NAME;
	public static String RenameWizardNamingPage_FILENAME_CANNOT_BE_NULL;
	public static String RenameWizardNamingPage_RENAME_WIZARD_NAMING_PAGE_DESCRIPTION;
	public static String RenameWizardNamingPage_RENAME_WIZARD_NAMING_PAGE_TITLE;
	public static String RenameWizardNamingPage_TRYING_TO_SET_FILENAME_TO_A_DISPOSED_OR;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
