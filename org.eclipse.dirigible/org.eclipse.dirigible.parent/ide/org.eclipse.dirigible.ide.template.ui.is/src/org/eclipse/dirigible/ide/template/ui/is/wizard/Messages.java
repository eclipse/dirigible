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

package org.eclipse.dirigible.ide.template.ui.is.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.template.ui.is.wizard.messages"; //$NON-NLS-1$
	public static String IntegrationServiceTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;
	public static String IntegrationServiceTemplateServiceParametersPage_CONDITION_PARAMETER_NAME;
	public static String IntegrationServiceTemplateServiceParametersPage_ENDPOINT_ADDRESS;
	public static String IntegrationServiceTemplateServiceParametersPage_INPUT_THE_CONDITION_PARAMETER_NAME;
	public static String IntegrationServiceTemplateServiceParametersPage_INPUT_THE_ENDPOINT_ADDRESS;
	public static String IntegrationServiceTemplateServiceParametersPage_INPUT_THE_ROUTE_IDENTIFIER;
	public static String IntegrationServiceTemplateServiceParametersPage_ORIGINAL_ENDPOINT_ADDRESS;
	public static String IntegrationServiceTemplateServiceParametersPage_ROUTE_IDENTIFIER;
	public static String IntegrationServiceTemplateServiceParametersPage_SERVICE_PARAMETERS;
	public static String IntegrationServiceTemplateServiceParametersPage_SET_THE_SERVICE_PARAAMETERS_WHICH_WILL_BE_USED_DURING_THE_GENERATION;
	public static String IntegrationServiceTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;
	public static String IntegrationServiceTemplateTargetLocationPage_TARGET_LOCATION;
	public static String IntegrationServiceTemplateTypePage_28;
	public static String IntegrationServiceTemplateTypePage_CONTENT_BASED_ROUTING_BY_HEADER_PARAMETER;
	public static String IntegrationServiceTemplateTypePage_PROXY_REST_SERVICE_SHIELDING_A_JAVA_SCRIPT_SERVICE;
	public static String IntegrationServiceTemplateTypePage_PROXY_REST_SERVICE_WITH_XSLT_TRANSFORMATION;
	public static String IntegrationServiceTemplateTypePage_SCHEDULED_JOB_TRIGGERING_A_JAVA_SCRIPT_SERVICE;
	public static String IntegrationServiceTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;
	public static String IntegrationServiceTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;
	public static String IntegrationServiceTemplateTypePage_WEB_SERVICE_PROVIDER_WITH_JAVA_SCRIPT_IMPLEMENTATION;
	public static String IntegrationServiceTemplateWizard_CREATE_INTEGRATION_SERVICE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
