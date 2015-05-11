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

package org.eclipse.dirigible.ide.template.ui.ed.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.template.ui.ed.wizard.messages"; //$NON-NLS-1$
	public static String ExtensionDefinitionTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;
	public static String ExtensionDefinitionTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;
	public static String ExtensionDefinitionTemplateTargetLocationPage_TARGET_LOCATION;
	public static String ExtensionDefinitionTemplateTypePage_MAIN_ACCESS_FILE;
	public static String ExtensionDefinitionTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;
	public static String ExtensionDefinitionTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;
	public static String ExtensionDefinitionTemplateWizard_CREATE_EXTENSION_FILE;
	public static String ExtensionsView_EXTENSIONS_ERROR;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
