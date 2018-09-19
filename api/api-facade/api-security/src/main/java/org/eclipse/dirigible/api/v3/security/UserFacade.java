/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.security;

import static java.text.MessageFormat.format;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.api.v3.http.HttpSessionFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class UserFacade.
 */
public class UserFacade implements IScriptingFacade {

	private static final String DIRIGIBLE_ANONYMOUS_USER_NAME_PROPERTY_NAME = "DIRIGIBLE_ANONYMOUS_USER_NAME_PROPERTY_NAME";

	private static final String DIRIGIBLE_ANONYMOUS_IDENTIFIER = "dirigible-anonymous-identifier";
	private static final String DIRIGIBLE_ANONYMOUS_USER = "dirigible-anonymous-user";

	private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);

	private static final String GUEST = "guest";

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public static final String getName() {
		// HTTP case
		String userName = null;
		userName = getRemoteUser();

		// Anonymous case
		if (userName == null) {
			userName = getAnonymousUser();
		}

		// Return HTTP, Anonymous or Local case
		return userName != null ? userName : GUEST;
	}

	/**
	 * Checks if the user is in role.
	 *
	 * @param role
	 *            the role
	 * @return true, if the user is in role
	 */
	public static final boolean isInRole(String role) {
		if (Configuration.isAnonymousModeEnabled() || Configuration.isAnonymousUserEnabled()) {
			return true;
		}
		try {
			return HttpRequestFacade.isUserInRole(role);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return false;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName
	 *            the user name
	 * @throws ContextException
	 *             in case of not initialized ThreadContext
	 */
	public static final void setName(String userName) throws ContextException {
		if (Configuration.isAnonymousModeEnabled()) {
			setContextProperty(DIRIGIBLE_ANONYMOUS_IDENTIFIER, userName);
			logger.warn(format("User name has been set programmatically {0} to the session as the anonymous mode is enabled", userName));
		} else if (Configuration.isAnonymousUserEnabled()) {
			setContextProperty(DIRIGIBLE_ANONYMOUS_USER, userName);
			logger.warn(format("User name has been set programmatically {0} to the session as the anonymous mode is enabled", userName));
		} else {
			throw new SecurityException("Setting the user name programmatically is supported only when the anonymous mode is enabled");
		}
	}

	/**
	 * Gets the user name by a given request as parameter.
	 *
	 * @return the user name
	 */
	public static final String getName(HttpServletRequest request) {
		if (request == null) {
			return getName();
		}
		// HTTP case
		String userName = null;
		try {
			userName = request.getRemoteUser();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (userName != null) {
			return userName;
		}
		return getName();
	}
	
	/**
	 * Gets the user name by a given request as parameter.
	 *
	 * @return the user name
	 */
	public static final String getName(Session session) {
		if (session == null) {
			return getName();
		}
		// WebSockets case
		String userName = null;
		try {
			userName = session.getUserPrincipal().getName();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (userName != null) {
			return userName;
		}
		return getName();
	}

	private static String getContextProperty(String property) throws ContextException {
		if (HttpSessionFacade.isValid()) {
			return HttpSessionFacade.getAttribute(property);
		} else if (ThreadContextFacade.isValid()) {
			Object value = ThreadContextFacade.get(property);
			if ((value != null) && (value instanceof String)) { 
				return (String) value;
			}
		}
		return null;
	}

	private static void setContextProperty(String property, String value) throws ContextException {
		if (HttpSessionFacade.isValid()) {
			HttpSessionFacade.setAttribute(property, value);
		} else {
			if (ThreadContextFacade.isValid()) {
				ThreadContextFacade.set(property, value);
			}
		}
	}

	private static String getRemoteUser() {
		try {
			if (HttpRequestFacade.isValid()) {
				return HttpRequestFacade.getRemoteUser();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private static String getAnonymousUser() {
		String userName = null;
		if (Configuration.isAnonymousModeEnabled()) {
			try {
				userName = getContextProperty(DIRIGIBLE_ANONYMOUS_IDENTIFIER);
			} catch (ContextException e) {
				logger.error(e.getMessage());
			}
		} else if (Configuration.isAnonymousUserEnabled()) {
			try {
				userName = getContextProperty(DIRIGIBLE_ANONYMOUS_USER);
				if (userName == null) {
					userName = setAnonymousUser();
				}
			} catch (ContextException e) {
				logger.error(e.getMessage());
			}
		}
		return userName;
	}

	private static String setAnonymousUser() {
		String userName = null;
		String anonymousUserNamePropertyName = Configuration.get(DIRIGIBLE_ANONYMOUS_USER_NAME_PROPERTY_NAME);
		if (anonymousUserNamePropertyName != null) {
			userName = Configuration.get(anonymousUserNamePropertyName);
			try {
				setName(userName);
			} catch (ContextException e) {
				logger.info("Error while setting userName from DIRIGIBLE_ANONYMOUS_USER_PROPERTY_NAME.", e);
			}
		}
		return userName;
	}
}
