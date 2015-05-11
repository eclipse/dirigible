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

package org.eclipse.dirigible.ide.template.ui.common;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.template.ui.common.messages"; //$NON-NLS-1$
	public static String GenerationModel_COULD_NOT_OPEN_INPUT_STREAM_FOR;
	public static String GenerationModel_NAME_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S;
	public static String GenerationModel_PATH_IS_NOT_VALID_FOR_A_RESOURCE_OF_THE_GIVEN_TYPE_S;
	public static String GenerationModel_RESOURCE_ALREADY_EXISTS_IN_THE_WORKSPACE;
	public static String GenerationModel_TEMPLATE_LOCATION_IS_EMPTY;
	public static String TemplateCommandHandler_COULD_NOT_OPEN_WIZARD;
	public static String TemplateCommandHandler_OPEN_WIZARD_ERROR;
	public static String TemplateFileCommandHandler_NO_FILE_IN_SELECTION_SELECTION_IS_EMPTY;
	public static String TemplateFileCommandHandler_NO_FILE_SELECTED_WILL_NOT_OPEN_WIZARD;
	public static String TemplateFileCommandHandler_SELECTED_RESOURCE_IS_NOT_A_FILE;
	public static String TemplateFileCommandHandler_UNKNOWN_SELECTION_TYPE;
	public static String TemplateGenerator_THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF;
	public static String TemplateTargetLocationPage_FILE_NAME;
	public static String TemplateTargetLocationPage_PACKAGE_NAME;
	public static String TemplateTargetLocationPage_INPUT_THE_FILE_NAME;
	public static String TemplateTargetLocationPage_INPUT_THE_PACKAGE_NAME;
	public static String TemplateTargetLocationPage_SELECT_THE_LOCATION_OF_THE_GENERATED_PAGE;
	public static String TemplateTypeWizardPage_AVAILABLE_TEMPLATES;
	public static String TemplateTypeWizardPage_ERROR_ON_LOADING_TEMPLATES_FOR_GENERATION;
	public static String TemplateTypeWizardPage_EXTENSION_POINT_0_COULD_NOT_BE_FOUND;
	public static String TemplateTypeWizardPage_SELECT_TEMPLATE_TYPE_FORM_THE_LIST;
	public static String TemplateWizard_GENERATION_FAILED;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
