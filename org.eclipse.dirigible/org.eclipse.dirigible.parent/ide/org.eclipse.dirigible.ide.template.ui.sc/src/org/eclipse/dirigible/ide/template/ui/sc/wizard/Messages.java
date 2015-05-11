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

package org.eclipse.dirigible.ide.template.ui.sc.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.template.ui.sc.wizard.messages"; //$NON-NLS-1$
	public static String SecurityConstraintTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;
	public static String SecurityConstraintTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;
	public static String SecurityConstraintTemplateTargetLocationPage_TARGET_LOCATION;
	public static String SecurityConstraintTemplateTypePage_MAIN_ACCESS_FILE;
	public static String SecurityConstraintTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;
	public static String SecurityConstraintTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;
	public static String SecurityConstraintTemplateWizard_CREATE_ACCESS_FILE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
