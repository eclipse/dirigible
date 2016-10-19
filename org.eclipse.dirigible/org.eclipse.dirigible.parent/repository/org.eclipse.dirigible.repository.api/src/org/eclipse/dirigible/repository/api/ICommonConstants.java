/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

public interface ICommonConstants {

	public static final String DIRIGIBLE_PRODUCT_NAME = "Eclipse Dirigible"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final String SEPARATOR = "/"; //$NON-NLS-1$
	public static final String DEBUG_SEPARATOR = ":"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$

	public static final String SANDBOX = "sandbox"; //$NON-NLS-1$
	public static final String REGISTRY = "registry"; //$NON-NLS-1$
	public static final String WORKSPACE = "workspace"; //$NON-NLS-1$

	public interface ARTIFACT_EXTENSION {
		public static final String JAVASCRIPT = "js"; //$NON-NLS-1$
		public static final String JSON = "json"; //$NON-NLS-1$
		public static final String SWAGGER = "swagger"; //$NON-NLS-1$
		public static final String ENTITY = "entity"; //$NON-NLS-1$
		public static final String RUBY = "rb"; //$NON-NLS-1$
		public static final String GROOVY = "groovy"; //$NON-NLS-1$
		public static final String JAVA = "java"; //$NON-NLS-1$
		public static final String COMMAND = "command"; //$NON-NLS-1$
		public static final String SQL = "sql"; //$NON-NLS-1$
		public static final String EXTENSION_POINT = "extensionpoint"; //$NON-NLS-1$
		public static final String EXTENSION = "extension"; //$NON-NLS-1$
		public static final String SECURITY = "access"; //$NON-NLS-1$
		public static final String FLOW = "flow"; //$NON-NLS-1$
		public static final String JOB = "job"; //$NON-NLS-1$
		public static final String LISTENER = "listener"; //$NON-NLS-1$
	}

	public interface ARTIFACT_TYPE {
		public final static String PROJECT_ROOT = "/"; //$NON-NLS-1$
		public final static String DATA_STRUCTURES = "DataStructures"; //$NON-NLS-1$
		public final static String INTEGRATION_SERVICES = "IntegrationServices"; //$NON-NLS-1$
		public final static String SCRIPTING_SERVICES = "ScriptingServices"; //$NON-NLS-1$
		public final static String TEST_CASES = "TestCases"; //$NON-NLS-1$
		public final static String WEB_CONTENT = "WebContent"; //$NON-NLS-1$
		public final static String SECURITY_CONSTRAINTS = "SecurityConstraints"; //$NON-NLS-1$
		public final static String WIKI_CONTENT = "WikiContent"; //$NON-NLS-1$
		public final static String MOBILE_APPLICATIONS = "MobileApplications"; //$NON-NLS-1$
		public final static String EXTENSION_DEFINITIONS = "ExtensionDefinitions"; //$NON-NLS-1$
		public static final String CONFIGURATION_SETTINGS = "ConfigurationSettings";
	}

	public interface TEMPLATE_TYPE {
		public final static String DATA_STRUCTURES = "DataStructures"; //$NON-NLS-1$
		public final static String INTEGRATION_SERVICES = "IntegrationServices"; //$NON-NLS-1$
		public final static String SCRIPTING_SERVICES = "ScriptingServices"; //$NON-NLS-1$
		public final static String TEST_CASES = "TestCases"; //$NON-NLS-1$
		public final static String WEB_CONTENT = "WebContent"; //$NON-NLS-1$
		public final static String WEB_CONTENT_FOR_ENTITY = "WebContentForEntity"; //$NON-NLS-1$
		public final static String SECURITY_CONSTRAINTS = "SecurityConstraints"; //$NON-NLS-1$
		public final static String WIKI_CONTENT = "WikiContent"; //$NON-NLS-1$
		public final static String MOBILE_APPLICATIONS = "MobileApplications"; //$NON-NLS-1$
		public final static String EXTENSION_DEFINITIONS = "ExtensionDefinitions"; //$NON-NLS-1$
	}

	public interface ENGINE_TYPE {
		public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
		public static final String JAVA = "java"; //$NON-NLS-1$
		public static final String GROOVY = "groovy"; //$NON-NLS-1$
		public static final String COMMAND = "command"; //$NON-NLS-1$
		public static final String CONDITION = "condition"; //$NON-NLS-1$
		public static final String FLOW = "flow"; //$NON-NLS-1$
		public static final String OUTPUT = "output"; //$NON-NLS-1$
		public static final String JOB = "job"; //$NON-NLS-1$
		public static final String WEB = "web"; //$NON-NLS-1$
		public static final String WIKI = "wiki"; //$NON-NLS-1$
		public static final String MOBILE = "mobile"; //$NON-NLS-1$
		public static final String SQL = "sql"; //$NON-NLS-1$
		public static final String TEST = "test"; //$NON-NLS-1$
	}

	public interface ENGINE_ALIAS {
		public static final String JAVASCRIPT = "js"; //$NON-NLS-1$
		public static final String JAVA = "java"; //$NON-NLS-1$
		public static final String GROOVY = "groovy"; //$NON-NLS-1$
		public static final String COMMAND = "command"; //$NON-NLS-1$
		public static final String FLOW = "flow"; //$NON-NLS-1$
		public static final String JOB = "job"; //$NON-NLS-1$
		public static final String WEB = "web"; //$NON-NLS-1$
		public static final String WIKI = "wiki"; //$NON-NLS-1$
		public static final String MOBILE = "mobile"; //$NON-NLS-1$
		public static final String SQL = "sql"; //$NON-NLS-1$
		public static final String TEST = "test"; //$NON-NLS-1$
	}

	public static final String DATA_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;

	public static final String WEB_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;

	public static final String WIKI_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;

	public static final String MOBILE_APPLICATIONS_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;

	public static final String SCRIPTING_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

	public static final String INTEGRATION_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;

	public static final String SECURITY_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;

	public static final String TESTS_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.TEST_CASES;

	public static final String EXTENSION_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;

	public static final String TEMPLATE_DEFINITIONS_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES;

	public static final String INITIAL_CONTEXT = "InitialContext"; //$NON-NLS-1$

	public static final String GUEST = "guest"; //$NON-NLS-1$

	public static final String COOKIE_ANONYMOUS_USER = "dirigible_anonymous_user"; //$NON-NLS-1$

	public static final String COOKIE_THEME = "dirigible_theme"; //$NON-NLS-1$

	// Platform specific
	public static final String MAIL_SESSION = "MailSession"; //$NON-NLS-1$

	public static final String MAIL_SESSION_PROVIDED = "MailSessionProvided"; //$NON-NLS-1$

	public static final String CONNECTIVITY_CONFIGURATION = "ConnectivityConfiguration"; //$NON-NLS-1$

	public static final String CMIS_CONFIGURATION = "CmisSession"; //$NON-NLS-1$

	public static final String PARAM_INSTANCE_NAME = "dirigibleInstanceName"; //$NON-NLS-1$

	public static final String LIFECYCLE_SERVICE = "LifecycleService"; //$NON-NLS-1$

	// Initi Parameters Names
	public static final String INIT_PARAM_RUNTIME_URL = "runtimeUrl"; //$NON-NLS-1$
	public static final String INIT_PARAM_SERVICES_URL = "servicesUrl"; //$NON-NLS-1$
	public static final String INIT_PARAM_ENABLE_ROLES = "enableRoles"; //$NON-NLS-1$
	public static final String INIT_PARAM_LOG_IN_SYSTEM_OUTPUT = "logInSystemOutput"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_DEFAULT_DATASOURCE = "jndiDefaultDataSource"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_CONNECTIVITY_CONFIGURATION = "jndiConnectivityService"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_MAIL_SESSION = "jndiMailService"; //$NON-NLS-1$
	public static final String INIT_PARAM_JDBC_SET_AUTO_COMMIT = "jdbcAutoCommit"; //$NON-NLS-1$
	public static final String INIT_PARAM_JDBC_MAX_CONNECTIONS_COUNT = "jdbcMaxConnectionsCount";
	public static final String INIT_PARAM_JDBC_WAIT_TIMEOUT = "jdbcWaitTimeout";
	public static final String INIT_PARAM_JDBC_WAIT_COUNT = "jdbcWaitCount";
	public static final String INIT_PARAM_REPOSITORY_PROVIDER = "repositoryProvider"; //$NON-NLS-1$
	public static final String INIT_PARAM_REPOSITORY_PROVIDER_MASTER = "repositoryProviderMaster"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_DATASOURCE_TYPE = "defaultDataSourceType"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_DATASOURCE_TYPE_JNDI = "jndi"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_DATASOURCE_TYPE_LOCAL = "local"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_MAIL_SERVICE = "mailSender"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_MAIL_SERVICE_PROVIDED = "provided"; //$NON-NLS-1$
	public static final String INIT_PARAM_DEFAULT_MAIL_SERVICE_BUILTIN = "built-in"; //$NON-NLS-1$
	public static final String INIT_PARAM_HOME_URL = "homeLocation"; //$NON-NLS-1$
	public static final String INIT_PARAM_RUN_TESTS_ON_INIT = "runTestsOnInit"; //$NON-NLS-1$
	public static final String INIT_PARAM_AUTO_ACTIVATE_ENABLED = "autoActivateEnabled"; //$NON-NLS-1$
	public static final String INIT_PARAM_AUTO_PUBLISH_ENABLED = "autoPublishEnabled"; //$NON-NLS-1$
	public static final String INIT_PARAM_ENABLE_SANDBOX = "enableSandbox"; //$NON-NLS-1$
	public static final String INIT_PARAM_LOCAL_REPOSITORY_ROOT_FOLDER = "localRepositoryRootFolder"; //$NON-NLS-1$
	public static final String INIT_PARAM_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE = "localRepositoryRootFolderIsAbsolute"; //$NON-NLS-1$
	// ---
}
