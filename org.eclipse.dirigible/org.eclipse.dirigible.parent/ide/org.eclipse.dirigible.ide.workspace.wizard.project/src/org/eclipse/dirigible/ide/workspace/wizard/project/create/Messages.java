/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.wizard.project.create;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.wizard.project.create.messages"; //$NON-NLS-1$
	public static String NewProjectWizard_COULD_NOT_CREATE_PROJECT;
	public static String NewProjectWizard_OPERATION_FAILED;
	public static String NewProjectWizard_PROJECT_S_CREATED_SUCCESSFULLY;
	public static String NewProjectWizard_WINDOW_TITLE;
	public static String NewProjectWizardMainPage_ENTER_PROJECT_NAME;
	public static String NewProjectWizardMainPage_PAGE_DESCRIPTION;
	public static String NewProjectWizardMainPage_PAGE_TITLE;
	public static String NewProjectWizardModel_ERROR_OCCURED_WHEN_TRYING_TO_VALIDATE_NEW_PROJECT_NAME;
	public static String NewProjectWizardModel_INVALID_PROJECT_NAME;
	public static String NewProjectWizardModel_PUBLISH_PROJECT_FAILED;
	public static String NewProjectWizardModel_PROJECT_WITH_NAME_S_WAS_ALREADY_CREATED_FROM_USER_S;
	public static String NewProjectWizardModel_PROJECT_WITH_THIS_NAME_ALREADY_EXISTS;
	public static String NewProjectWizardTemplateTypePage_AVAILABLE_TEMPLATES;
	public static String NewProjectWizardTemplateTypePage_ERROR_ON_LOADING_TEMPLATES_FOR_GENERATION;
	public static String NewProjectWizardTemplateTypePage_PAGE_DESCRIPTION;
	public static String NewProjectWizardTemplateTypePage_PAGE_NAME;
	public static String NewProjectWizardTemplateTypePage_PAGE_TITLE;
	public static String NewProjectWizardTemplateTypePage_SELECT_TEMPLATE_TYPE_FORM_THE_LIST;
	public static String ProjectTemplateType_PROJECT_TEMPLATE_CONTENT_DOES_NOT_EXIST_AT_S;
	public static String ProjectTemplateType_PROJECT_TEMPLATE_IMAGE_DOES_NOT_EXIST_AT_S;
	public static String ProjectTemplateType_PROJECT_TEMPLATE_LOCATION_S_IS_NOT_VALID;
	public static String ProjectTemplateType_PROJECT_TEMPLATE_METADATA_DOES_NOT_EXIST_AT_S;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
