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
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisInjector implements IInjector {

	private static final String PARAM_PASSWORD = "Password";

	private static final String PARAM_USER = "User";

	public static final String CMIS_CONFIGURATION = "CmisSession"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(CmisInjector.class.getCanonicalName());

	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Object cmisConfiguration = System.getProperties().get(CMIS_CONFIGURATION);
		if (cmisConfiguration == null) {
			try {
				cmisConfiguration = lookupCmisConfiguration();
				if (cmisConfiguration != null) {
					req.setAttribute(CMIS_CONFIGURATION, cmisConfiguration);
					System.getProperties().put(CMIS_CONFIGURATION, cmisConfiguration);
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}
	}

	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {

		Object cmisConfiguration = System.getProperties().get(CMIS_CONFIGURATION);
		if (cmisConfiguration == null) {
			try {
				cmisConfiguration = lookupCmisConfiguration();
				if (cmisConfiguration != null) {
					System.getProperties().put(CMIS_CONFIGURATION, cmisConfiguration);
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}
	}

	/**
	 * Retrieve the CMIS Configuration from the target platform
	 *
	 * @return
	 * @throws NamingException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private Object lookupCmisConfiguration() throws NamingException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final InitialContext ctx = new InitialContext();
		String key = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION);
		if (key != null) {
			Object ecmService = ctx.lookup(key);
			if (ecmService != null) {
				String authMethod = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_AUTH);
				logger.debug(String.format("CMIS Authentication Method: %s", authMethod));
				String uniqueName = null;
				String secretKey = null;
				if (InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_AUTH_KEY.equals(authMethod)) {
					uniqueName = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_NAME);
					secretKey = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_KEY);
				} else if (InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_AUTH_DEST.equals(authMethod)) {
					String destinationName = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_DESTINATION);
					Properties destinationPropeties = initializeFromDestination(destinationName);
					uniqueName = destinationPropeties.getProperty(PARAM_USER);
					secretKey = destinationPropeties.getProperty(PARAM_PASSWORD);
				} else {
					logger.error(String.format("Connection to CMIS Repository was failed. Invalid Authentication Method: %s", authMethod));
					return null;
				}
				logger.debug("Connecting to CMIS Repository with name: %s and key: %s", uniqueName, secretKey);
				try {
					Method connectMethod = ecmService.getClass().getMethod("connect", String.class, String.class);
					Object openCmisSession = connectMethod.invoke(ecmService, uniqueName, secretKey);
					if (openCmisSession != null) {
						logger.debug("Connection to CMIS Repository was successful.");
						return openCmisSession;
					}
				} catch (Exception e) {
					logger.error("Connection to CMIS Repository was failed.", e);
				}
			} else {
				logger.error("ECM Service is requested, but not available");
			}
		}
		return null;
	}

	private Properties initializeFromDestination(String destinationName) throws NamingException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug(String.format("CMIS Lookup Destination: %s", destinationName));
		Object connectivityService = ConnectivityInjector.lookupConnectivityConfiguration();
		Method connectivityMethod = connectivityService.getClass().getMethod("getConnectivityConfiguration");
		Object configuration = connectivityMethod.invoke(connectivityService);
		Method configurationMethod = configuration.getClass().getMethod("getConfiguration", String.class);
		Object destinationConfiguration = configurationMethod.invoke(configuration, destinationName);
		Method propertiesMethod = destinationConfiguration.getClass().getMethod("getAllProperties");
		Properties destinationPropeties = (Properties) propertiesMethod.invoke(destinationConfiguration);
		logger.debug(String.format("CMIS Destination Properties: %s", getPropertiesAsString(destinationPropeties)));
		return destinationPropeties;
	}

	private static String getPropertiesAsString(Properties prop) {
		if (prop == null) {
			return "null properties";
		}
		StringWriter writer = new StringWriter();
		try {
			prop.store(writer, "");
		} catch (IOException e) {
			logger.error("Connection to CMIS Repository was failed.", e);
		}
		return writer.getBuffer().toString();
	}

}
