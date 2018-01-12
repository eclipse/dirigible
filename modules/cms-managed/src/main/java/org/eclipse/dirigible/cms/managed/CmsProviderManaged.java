/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.cms.managed;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsProviderManaged implements ICmsProvider {
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME = "java:comp/env/EcmService"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY = "key"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST = "destination"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME. */
	public static final String DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME = "DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME"; //$NON-NLS-1$

	/** The Constant NAME. */
	public static final String NAME = "cmis"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "managed"; //$NON-NLS-1$

	private static final String PARAM_USER = "User"; //$NON-NLS-1$
	private static final String PARAM_PASSWORD = "Password"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(CmsProviderManaged.class);
	
	private Object cmisSession;
	
	public CmsProviderManaged() {
		try {
			this.cmisSession = lookupCmisSession();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NamingException e) {
			logger.error("Error in initializing the managed CMIS session", e);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Object getSession() {
		if (this.cmisSession == null) {
			throw new IllegalStateException("Managed CMIS Session has not been initialized properly.");
		}
		return this.cmisSession;
	}
	
	/**
	 * Retrieve the CMIS Configuration from the target platform
	 *
	 * @return the managed CMIS session
	 * @throws NamingException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private Object lookupCmisSession() throws NamingException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final InitialContext ctx = new InitialContext();
		Configuration.load("/dirigible-cms.properties");
		String key = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME);
		if (key != null) {
			Object ecmService = ctx.lookup(key);
			if (ecmService != null) {
				String authMethod = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD);
				logger.debug(String.format("CMIS Authentication Method: %s", authMethod));
				String uniqueName = null;
				String secretKey = null;
				if (DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY.equals(authMethod)) {
					uniqueName = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME);
					secretKey = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY);
				} else if (DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST.equals(authMethod)) {
					String destinationName = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION);
					Properties destinationProperties = initializeFromDestination(destinationName);
					uniqueName = destinationProperties.getProperty(PARAM_USER);
					secretKey = destinationProperties.getProperty(PARAM_PASSWORD);
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
		Object connectivityService = lookupConnectivityConfiguration();
		Method connectivityMethod = connectivityService.getClass().getMethod("getConnectivityConfiguration");
		Object configuration = connectivityMethod.invoke(connectivityService);
		Method configurationMethod = configuration.getClass().getMethod("getConfiguration", String.class);
		Object destinationConfiguration = configurationMethod.invoke(configuration, destinationName);
		Method propertiesMethod = destinationConfiguration.getClass().getMethod("getAllProperties");
		Properties destinationProperties = (Properties) propertiesMethod.invoke(destinationConfiguration);
		logger.debug(String.format("CMIS Destination Properties: %s", getPropertiesAsString(destinationProperties)));
		return destinationProperties;
	}
	
	/**
	 * Retrieve the Connectivity Configuration from the target platform
	 *
	 * @return the managed connectivity service proxy
	 * @throws NamingException
	 */
	static Object lookupConnectivityConfiguration() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = Configuration.get(DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME);
		if (key != null) {
			return ctx.lookup(key);
		}
		return null;
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
