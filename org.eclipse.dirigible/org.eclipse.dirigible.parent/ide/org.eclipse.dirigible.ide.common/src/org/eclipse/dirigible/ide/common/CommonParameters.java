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

package org.eclipse.dirigible.ide.common;

import javax.servlet.http.HttpServletRequest;



import org.eclipse.dirigible.ide.common.dual.DualParameters;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class CommonParameters {

	
	public static String getWorkspace() {
		return IRepositoryPaths.DB_DIRIGIBLE_USERS + getUserName() + ICommonConstants.SEPARATOR + IRepositoryPaths.WORKSPACE_FOLDER_NAME;
	}

	public static final String DB_DIRIGIBLE_CONFIGURATIONS = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.CONFIGURATION_SETTINGS; //$NON-NLS-1$

	
	public static final String LOGGER_FACTORY = "loggerFactory"; //$NON-NLS-1$

	public static String getWebContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	public static String getWikiContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
	}

	public static String getScriptingContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}
	
	public static String getIntegrationContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

	public static String getSecuritContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;
	}

	public static String getTestingContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.TEST_CASES;
	}

	public static String getExtensionContentSandbox() {
		return IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + getUserName() + ICommonConstants.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;
	}

	// github url
	public static final String GIT_REPOSITORY_URL = "https://github.com/SAP/cloud-dirigible-samples.git";
	
	public static final String WEB_CONTENT_CONTAINER_MAPPING = "/web"; //$NON-NLS-1$
	public static final String JAVASCRIPT_CONTAINER_MAPPING = "/js"; //$NON-NLS-1$
	public static final String JAVASCRIPT_DEBUG_CONTAINER_MAPPING = "/js-debug"; //$NON-NLS-1$
	public static final String RUBY_CONTAINER_MAPPING = "/rb"; //$NON-NLS-1$
	public static final String GROOVY_CONTAINER_MAPPING = "/groovy"; //$NON-NLS-1$
	public static final String JAVA_CONTAINER_MAPPING = "/java"; //$NON-NLS-1$
	public static final String COMMAND_CONTAINER_MAPPING = "/command"; //$NON-NLS-1$
	public static final String TEST_CASES_CONTAINER_MAPPING = "/test"; //$NON-NLS-1$
	public static final String WIKI_CONTENT_CONTAINER_MAPPING = "/wiki"; //$NON-NLS-1$
	public static final String FLOW_CONTAINER_MAPPING = "/flow"; //$NON-NLS-1$
	public static final String JOB_CONTAINER_MAPPING = "/job"; //$NON-NLS-1$

	public static final String WEB_CONTENT_SANDBOX_MAPPING = "/web-sandbox"; //$NON-NLS-1$
	public static final String JAVASCRIPT_SANDBOX_MAPPING = "/js-sandbox"; //$NON-NLS-1$
	public static final String RUBY_SANDBOX_MAPPING = "/rb-sandbox"; //$NON-NLS-1$
	public static final String GROOVY_SANDBOX_MAPPING = "/groovy-sandbox"; //$NON-NLS-1$
	public static final String JAVA_SANDBOX_MAPPING = "/java-sandbox"; //$NON-NLS-1$
	public static final String COMMAND_SANDBOX_MAPPING = "/command-sandbox"; //$NON-NLS-1$
	public static final String TEST_CASES_SANDBOX_MAPPING = "/test-sandbox"; //$NON-NLS-1$
	public static final String WIKI_CONTENT_SANDBOX_MAPPING = "/wiki-sandbox"; //$NON-NLS-1$
	public static final String FLOW_SANDBOX_MAPPING = "/flow-sandbox"; //$NON-NLS-1$
	public static final String JOB_SANDBOX_MAPPING = "/job-sandbox"; //$NON-NLS-1$

	public static final String JAVASCRIPT_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT; //$NON-NLS-1$
	public static final String RUBY_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.RUBY; //$NON-NLS-1$
	public static final String GROOVY_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.GROOVY; //$NON-NLS-1$
	public static final String JAVA_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.JAVA; //$NON-NLS-1$
	public static final String COMMAND_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.COMMAND; //$NON-NLS-1$
	public static final String FLOW_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.FLOW; //$NON-NLS-1$
	public static final String JOB_SERVICE_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.JOB; //$NON-NLS-1$
	
	
	public static final String EXTENSION_POINT_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.EXTENSION_POINT; //$NON-NLS-1$
	public static final String EXTENSION_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.EXTENSION; //$NON-NLS-1$
	public static final String SECURITY_EXTENSION = ICommonConstants.ARTIFACT_EXTENSION.SECURITY; //$NON-NLS-1$
	
	

	public static final String CONTENT_EXPORT = "/content-export/"; //$NON-NLS-1$

	

	// Workbench Parameters
	public static final String PARAMETER_PERSPECTIVE = "perspective"; //$NON-NLS-1$
	public static final String PARAMETER_PROJECT = "project"; //$NON-NLS-1$
	public static final String PARAMETER_PACKAGE = "package"; //$NON-NLS-1$

	public static final String DATABASE_PRODUCT_NAME = "DATABASE_PRODUCT_NAME"; //$NON-NLS-1$
	public static final String DATABASE_PRODUCT_VERSION = "DATABASE_PRODUCT_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_MINOR_VERSION = "DATABASE_MINOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_MAJOR_VERSION = "DATABASE_MAJOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_NAME = "DATABASE_DRIVER_NAME"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_MINOR_VERSION = "DATABASE_DRIVER_MINOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_MAJOR_VERSION = "DATABASE_DRIVER_MAJOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_CONNECTION_CLASS_NAME = "DATABASE_CONNECTION_CLASS_NAME"; //$NON-NLS-1$

	public static final String DIRIGIBLE_DEBUGGER_BRIDGE = "dirigible.debugger.bridge"; //$NON-NLS-1$
	public static final String DIRIGIBLE_RUNTIME_BRIDGE = "dirigible.runtime.bridge"; //$NON-NLS-1$

	public static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$
	public static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$
	public static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$
	public static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$
	public static final String ALIAS = "ALIAS"; //$NON-NLS-1$
	public static final String VIEW = "VIEW"; //$NON-NLS-1$
	public static final String TABLE = "TABLE"; //$NON-NLS-1$

	public static final String[] TABLE_TYPES = { TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY,
			LOCAL_TEMPORARY, SYSTEM_TABLE };

	

	public static String getDatabaseProductName() {
		return get(DATABASE_PRODUCT_NAME);
	}

	public static String getDatabaseProductVersion() {
		return get(DATABASE_PRODUCT_VERSION);
	}

	public static String getDriverName() {
		return get(DATABASE_DRIVER_NAME);
	}



	public static final String CONF_PATH_IDE = "/ide";
	
	public static final String CONF_PATH_GENERIC_VIEWS = "/ide/generic/views";

	public static final int[] BINARY_TYPES = new int[]{
			java.sql.Types.ARRAY,
			java.sql.Types.BINARY,
			java.sql.Types.BIT,
			java.sql.Types.BIT,
			java.sql.Types.BLOB,
			java.sql.Types.CLOB,
			java.sql.Types.DATALINK,
			java.sql.Types.DISTINCT,
			java.sql.Types.JAVA_OBJECT,
			java.sql.Types.LONGVARBINARY,
			java.sql.Types.NCLOB,
			java.sql.Types.NULL,
			java.sql.Types.OTHER,
			java.sql.Types.REF,
			java.sql.Types.SQLXML,
			java.sql.Types.STRUCT,
			java.sql.Types.VARBINARY
	};
	
	
	
	
// =====================================================================================================================================
// DUAL PARAMETERS - DEPENDING ON THE TARGET PLATFORM - RAP or RCP
// =====================================================================================================================================

		public static String get(String name) {
			return DualParameters.get(name);
		}
		
		public static Object getObject(String name) {
			return DualParameters.getObject(name);
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
			return DualParameters.getUserName();
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
	
	public static final void initSystemParameters() {
		DualParameters.initSystemParameters();
	}
	
	
	
	
}
