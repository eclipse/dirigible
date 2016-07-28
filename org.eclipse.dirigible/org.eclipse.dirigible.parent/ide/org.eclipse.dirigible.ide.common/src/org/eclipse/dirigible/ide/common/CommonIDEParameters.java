/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.common;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.common.dual.DualParameters;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
// import org.eclipse.dirigible.repository.datasource.DataSourceFacade;

public class CommonIDEParameters {

	public static String getWorkspace() {
		return IRepositoryPaths.DB_DIRIGIBLE_USERS + getUserName() + ICommonConstants.SEPARATOR + IRepositoryPaths.WORKSPACE_FOLDER_NAME;
	}

	public static final String DB_DIRIGIBLE_CONFIGURATIONS = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.CONFIGURATION_SETTINGS;

	public static final String LOGGER_FACTORY = "loggerFactory"; //$NON-NLS-1$

	public static String getWebContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	public static String getWikiContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
	}

	public static String getMobileApplicationsSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;
	}

	public static String getScriptingContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

	public static String getIntegrationContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

	public static String getSecuritContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;
	}

	public static String getTestingContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.TEST_CASES;
	}

	public static String getExtensionContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;
	}

	// github url
	public static final String GIT_REPOSITORY_URL = "https://github.com/eclipse/dirigible-samples.git";

	public static final String WEB_CONTENT_CONTAINER_MAPPING = "/web"; //$NON-NLS-1$
	public static final String JAVASCRIPT_CONTAINER_MAPPING = "/js"; //$NON-NLS-1$
	public static final String JAVASCRIPT_DEBUG_CONTAINER_MAPPING = "/js-debug"; //$NON-NLS-1$
	public static final String RUBY_CONTAINER_MAPPING = "/rb"; //$NON-NLS-1$
	public static final String GROOVY_CONTAINER_MAPPING = "/groovy"; //$NON-NLS-1$
	public static final String JAVA_CONTAINER_MAPPING = "/java"; //$NON-NLS-1$
	public static final String COMMAND_CONTAINER_MAPPING = "/command"; //$NON-NLS-1$
	public static final String TEST_CASES_CONTAINER_MAPPING = "/test"; //$NON-NLS-1$
	public static final String WIKI_CONTENT_CONTAINER_MAPPING = "/wiki"; //$NON-NLS-1$
	public static final String MOBILE_APPLICATIONS_CONTAINER_MAPPING = "/mobile"; //$NON-NLS-1$
	public static final String FLOW_CONTAINER_MAPPING = "/flow"; //$NON-NLS-1$
	public static final String JOB_CONTAINER_MAPPING = "/job"; //$NON-NLS-1$
	public static final String LISTENER_CONTAINER_MAPPING = "/listener"; //$NON-NLS-1$
	public static final String SQL_CONTAINER_MAPPING = "/sql"; //$NON-NLS-1$

	public static final String WEB_CONTENT_SANDBOX_MAPPING = "/web-sandbox"; //$NON-NLS-1$
	public static final String JAVASCRIPT_SANDBOX_MAPPING = "/js-sandbox"; //$NON-NLS-1$
	public static final String RUBY_SANDBOX_MAPPING = "/rb-sandbox"; //$NON-NLS-1$
	public static final String GROOVY_SANDBOX_MAPPING = "/groovy-sandbox"; //$NON-NLS-1$
	public static final String JAVA_SANDBOX_MAPPING = "/java-sandbox"; //$NON-NLS-1$
	public static final String COMMAND_SANDBOX_MAPPING = "/command-sandbox"; //$NON-NLS-1$
	public static final String TEST_CASES_SANDBOX_MAPPING = "/test-sandbox"; //$NON-NLS-1$
	public static final String WIKI_CONTENT_SANDBOX_MAPPING = "/wiki-sandbox"; //$NON-NLS-1$
	public static final String MOBILE_APPLICATIONS_SANDBOX_MAPPING = "/mobile-sandbox"; //$NON-NLS-1$
	public static final String FLOW_SANDBOX_MAPPING = "/flow-sandbox"; //$NON-NLS-1$
	public static final String JOB_SANDBOX_MAPPING = "/job-sandbox"; //$NON-NLS-1$
	public static final String SQL_SANDBOX_MAPPING = "/sql-sandbox"; //$NON-NLS-1$
	public static final String LISTENER_SANDBOX_MAPPING = "/listener-sandbox"; //$NON-NLS-1$

	public static final String CONTENT_EXPORT = "/content-export/"; //$NON-NLS-1$

	// Workbench Parameters
	public static final String PARAMETER_PERSPECTIVE = "perspective"; //$NON-NLS-1$
	public static final String PARAMETER_PROJECT = "project"; //$NON-NLS-1$
	public static final String PARAMETER_PACKAGE = "package"; //$NON-NLS-1$

	// public static final String DIRIGIBLE_DEBUGGER_BRIDGE = "dirigible.debugger.bridge"; //$NON-NLS-1$
	// public static final String DIRIGIBLE_RUNTIME_BRIDGE = "dirigible.runtime.bridge"; //$NON-NLS-1$

	private static final String SELECTED_DATASOURCE_NAME = "SELECTED_DATASOURCE_NAME"; //$NON-NLS-1$

	public static final String CONF_PATH_IDE = "/ide";

	public static final String CONF_PATH_GENERIC_VIEWS = "/ide/generic/views";

	public static final int[] BINARY_TYPES = new int[] { java.sql.Types.ARRAY, java.sql.Types.BINARY, java.sql.Types.BIT, java.sql.Types.BIT,
			java.sql.Types.BLOB, java.sql.Types.CLOB, java.sql.Types.DATALINK, java.sql.Types.DISTINCT, java.sql.Types.JAVA_OBJECT,
			java.sql.Types.LONGVARBINARY, java.sql.Types.NCLOB, java.sql.Types.NULL, java.sql.Types.OTHER, java.sql.Types.REF, java.sql.Types.SQLXML,
			java.sql.Types.STRUCT, java.sql.Types.VARBINARY };

	// =====================================================================================================================================
	// DUAL PARAMETERS - DEPENDING ON THE TARGET PLATFORM - RAP or RCP
	// =====================================================================================================================================

	public static String get(String name) {
		return DualParameters.get(name);
	}

	public static Object getObject(String name) {
		return DualParameters.getObject(name);
	}

	public static Object getObject(String name, HttpServletRequest request) {
		return DualParameters.getObject(name, request);
	}

	public static void set(String name, String value) {
		DualParameters.set(name, value);
	}

	public static void setObject(String name, Object value) {
		DualParameters.setObject(name, value);
	}

	public static String getRuntimeUrl() {
		return DualParameters.getRuntimeUrl();
	}

	public static Object getService(Class clazz) {
		return DualParameters.getService(clazz);
	}

	public static String getServicesUrl() {
		return DualParameters.getServicesUrl();
	}

	public static String getContextPath() {
		return DualParameters.getContextPath();
	}

	public static HttpServletRequest getRequest() {
		return DualParameters.getRequest();
	}

	public static Boolean isRolesEnabled() {
		return DualParameters.isRolesEnabled();
	}

	public static String getUserName() {
		return DualParameters.getUserName(null);
	}

	public static String getUserName(HttpServletRequest request) {
		return DualParameters.getUserName(request);
	}

	public static boolean isUserInRole(String role) {
		return DualParameters.isUserInRole(role);
	}

	public static String getSessionId() {
		return DualParameters.getSessionId();
	}

	public static final boolean isRAP() {
		return DualParameters.isRAP();
	}

	public static final boolean isRCP() {
		return DualParameters.isRCP();
	}

	// public static final void initSystemParameters() {
	// DualParameters.initSystemParameters();
	// }

	public static boolean isAutoPublishEnabled() {
		return DualParameters.isAutoPublishEnabled();
	}

	public static boolean isAutoActivateEnabled() {
		return DualParameters.isAutoActivateEnabled();
	}

	public static boolean isSandboxEnabled() {
		return DualParameters.isSandboxEnabled();
	}

	public static void setAutoActivate(boolean checked) {
		DualParameters.set(DualParameters.SET_AUTO_ACTIVATE, checked);
	}

	public static void setAutoPublish(boolean checked) {
		DualParameters.set(DualParameters.SET_AUTO_PUBLISH, checked);
	}

	public static String getSelectedDatasource() {
		return get(SELECTED_DATASOURCE_NAME);
	}

	public static void setSelectedDatasource(String dsName) {
		set(SELECTED_DATASOURCE_NAME, dsName);
	}

}
