/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitParametersInjector implements IInjector {

	private static final Logger logger = LoggerFactory.getLogger(InitParametersInjector.class.getCanonicalName());

	// Initi Parameters Names
	public static final String INIT_PARAM_RUNTIME_URL = "runtimeUrl"; //$NON-NLS-1$
	public static final String INIT_PARAM_SERVICES_URL = "servicesUrl"; //$NON-NLS-1$
	public static final String INIT_PARAM_ENABLE_ROLES = "enableRoles"; //$NON-NLS-1$
	public static final String INIT_PARAM_LOG_IN_SYSTEM_OUTPUT = "logInSystemOutput"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_DEFAULT_DATASOURCE = "jndiDefaultDataSource"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_CONNECTIVITY_CONFIGURATION = "jndiConnectivityService"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_CMIS_CONFIGURATION = "jndiCmisService"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_CMIS_CONFIGURATION_NAME = "jndiCmisServiceName"; //$NON-NLS-1$
	public static final String INIT_PARAM_JNDI_CMIS_CONFIGURATION_KEY = "jndiCmisServiceKey"; //$NON-NLS-1$
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

	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String parameterValue = null;
			if (System.getProperties().containsKey(parameterName)) {
				parameterValue = get(parameterName);
				logger.debug(String.format("Initial Parameter per Request retreived from the Environment: name=%s value=%s", parameterName,
						parameterValue));
			} else {
				parameterValue = servletConfig.getInitParameter(parameterName);
				logger.debug(String.format("Initial Parameter per Request retreived from the Servlet Configuration: name=%s value=%s", parameterName,
						parameterValue));
			}
			req.setAttribute(parameterName, parameterValue);
		}
	}

	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {

		Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String parameterValue = null;
			if (System.getProperties().containsKey(parameterName)) {
				parameterValue = get(parameterName);
				logger.info(String.format("Initial Parameter exists in the Environment: name=%s value=%s", parameterName, parameterValue));
				System.out.println(String.format("Initial Parameter exists in the Environment: name=%s value=%s", parameterName, parameterValue));
			} else {
				parameterValue = servletConfig.getInitParameter(parameterName);
				System.getProperties().put(parameterName, parameterValue);
				logger.info(String.format("Initial Parameter set to the Environment: name=%s value=%s", parameterName, parameterValue));
				System.out.println(String.format("Initial Parameter set to the Environment: name=%s value=%s", parameterName, parameterValue));
			}
		}
	}

	public static String get(String key) {
		return System.getProperty(key);
	}

}
