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
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME"; //$NON-NLS-1$
	
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
			try {
				this.cmisSession = lookupCmisSession();
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NamingException e) {
				String message = "Error in initializing the managed CMIS session";
				logger.error(message, e);
				throw new IllegalStateException(message, e);
			}
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
	public Object lookupCmisSession() throws NamingException, NoSuchMethodException, SecurityException, IllegalAccessException,
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
					Properties destinationPropeties = initializeFromDestination(destinationName);
					uniqueName = destinationPropeties.getProperty(PARAM_USER);
					secretKey = destinationPropeties.getProperty(PARAM_PASSWORD);
				} else {
					String message = String.format("Connection to CMIS Repository was failed. Invalid Authentication Method: %s", authMethod);
					logger.error(message);
					throw new SecurityException(message);
				}
				logger.debug(String.format("Connecting to CMIS Repository with name: %s and key: %s", uniqueName, secretKey));
				try {
					Method connectMethod = ecmService.getClass().getMethod("connect", String.class, String.class);
					Object openCmisSession = connectMethod.invoke(ecmService, uniqueName, secretKey);
					if (openCmisSession != null) {
						logger.debug("Connection to CMIS Repository was successful.");
						return openCmisSession;
					}
				} catch (Throwable t) {
					String message = "Connection to CMIS Repository was failed.";
					logger.error(message, t);
					throw new IllegalStateException(message, t);
				}
			} else {
				String message = "ECM is requested as CMIS service, but it is not available.";
				logger.error(message);
				throw new IllegalStateException(message);
			}
		} else {
			String message = "CMIS service JNDI name has not been provided.";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		String message = "Initializing the managed CMIS session failed.";
		logger.error(message);
		throw new IllegalStateException(message);
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
		Properties destinationPropeties = (Properties) propertiesMethod.invoke(destinationConfiguration);
		logger.debug(String.format("CMIS Destination Properties: %s", getPropertiesAsString(destinationPropeties)));
		return destinationPropeties;
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
