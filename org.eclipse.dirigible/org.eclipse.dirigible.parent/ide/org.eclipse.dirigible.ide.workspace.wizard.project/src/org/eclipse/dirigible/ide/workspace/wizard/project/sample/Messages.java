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

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.wizard.project.sample.messages";//$NON-NLS-1$
	public static String SampleProjectWizard_COULD_NOT_CREATE_PROJECT;
	public static String SampleProjectWizard_OPERATION_FAILED ;
	public static String SampleProjectWizard_PROJECT_S_CREATED_SUCCESSFULLY;
	public static String SampleProjectWizardGitTemplatePage_ERROR_ON_LOADING_GIT_TEMPLATES_FOR_GENERATION;
	public static String SampleProjectWizardGitTemplatePage_PAGE_NAME;
	public static String SampleProjectWizardGitTemplatePage_PAGE_TITLE;
	public static String SampleProjectWizardGitTemplatePage_PAGE_DESCRIPTION;
	public static String SampleProjectWizardMainPage_PAGE_NAME;
	public static String SampleProjectWizardMainPage_PAGE_TITLE;
	public static String SampleProjectWizardMainPage_ENTER_PROJECT_NAME;
	public static String SampleProjectWizardMainPage_PAGE_DESCRIPTION;
	public static String SampleProjectWizard_WINDOW_TITLE;
	public static String SampleProjectWizardGitTemplatePage_AVAILABLE_GIT_TEMPLATES;
	public static String SampleProjectWizardGitTemplatePage_SELECT_TEMPLATE_TYPE_FORM_THE_LIST;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
