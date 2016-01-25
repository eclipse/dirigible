/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("javadoc")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.template.ui.mobile.wizard.messages"; //$NON-NLS-1$
	public static String MobileTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;
	public static String MobileTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;
	public static String MobileTemplateTargetLocationPage_TARGET_LOCATION;

	public static String MobileTemplateTitlePage_PAGE_TITLE;

	public static String MobileTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;
	public static String MobileTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;
	public static String MobileTemplateWizard_CREATE_MOBILE_APP;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
