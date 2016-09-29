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
import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisInjector implements IInjector {

	public static final String CMIS_CONFIGURATION = "CmisSession"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(CmisInjector.class);

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
	 */
	private Object lookupCmisConfiguration() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION);
		if (key != null) {
			Object ecmService = ctx.lookup(key);
			if (ecmService != null) {
				String uniqueName = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_NAME);
				String secretKey = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_CMIS_CONFIGURATION_KEY);
				logger.debug("Connecting to CMIS Repository with name: %s and key: %s", uniqueName, secretKey);
				try {
					Method connectMethod = ecmService.getClass().getMethod("connect", String.class, String.class);
					Object openCmisSession = connectMethod.invoke(ecmService, uniqueName, secretKey);
					if (openCmisSession != null) {
						logger.debug("Connection to CMIS Repository was successful.");
						return openCmisSession;
					}
				} catch (Exception e) {
					logger.error("Connection to CMIS Repository was NOT successful.", e);
				}
			}
		}
		return null;
	}

}
