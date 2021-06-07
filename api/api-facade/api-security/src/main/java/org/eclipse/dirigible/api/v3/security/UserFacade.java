/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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
	private static final String DIRIGIBLE_JWT_USER = "dirigible-jwt-user";
	private static final String NO_VALID_REQUEST = "Trying to use HTTP Session Facade without a valid Session (HTTP Request/Response)";
	private static final String INVOCATION_COUNT = "invocation.count";
	private static final String LANGUAGE_HEADER = "accept-language";

	private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);

	private static final String GUEST = "guest";
	private static final String AUTH = "authorization";

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
			logger.debug(format("User name has been set programmatically {0} to the session as the anonymous mode is enabled", userName));
		} else if (Configuration.isAnonymousUserEnabled()) {
			setContextProperty(DIRIGIBLE_ANONYMOUS_USER, userName);
			logger.debug(format("User name has been set programmatically {0} to the session as the anonymous mode is enabled", userName));
		} else if (Configuration.isJwtModeEnabled()) {
			setContextProperty(DIRIGIBLE_JWT_USER, userName);
			logger.debug(format("User name has been set programmatically {0} to the session as the JWT mode is enabled", userName));
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

	public static final Integer getTimeout() {
		if (HttpSessionFacade.isValid()) {
			return HttpSessionFacade.getMaxInactiveInterval();
		} else {
			logger.error(NO_VALID_REQUEST);
		}
		return 0;
	}

	public static String getAuthType() {
		if (HttpRequestFacade.isValid()) {
			return HttpRequestFacade.getAuthType();
		} else {
			logger.error(NO_VALID_REQUEST);
		}
		return null;
	}

	/**
	 * The Authorization header returns the type + token.
	 * Substring from the empty space to only get the token.
	 */
	public static String getSecurityToken() {
		if (HttpRequestFacade.isValid()) {
			String token = HttpRequestFacade.getHeader(AUTH);
			return token != null && !"".equals(token)? token.substring(token.indexOf(" ")) : "";
		} else {
			logger.error(NO_VALID_REQUEST);
		}
		return "";
	}

	public static String getInvocationCount() {
		if (HttpSessionFacade.isValid()) {
			return HttpSessionFacade.getAttribute(INVOCATION_COUNT);
		} else {
			logger.error(NO_VALID_REQUEST);
		}
		return null;
	}

	/**
	 * The accept-language attribute returns multiple values.
	 * Eg. en-GB,en-US;q=0.9,en;q=0.8
	 * Substring until the semicolon to get the IETF (BCP 47) format.
	 */
	public static String getLanguage() {
		if (HttpRequestFacade.isValid()) {
			String language = HttpRequestFacade.getHeader(LANGUAGE_HEADER);
			return language != null && !"".equals(language)? language.substring(0, language.indexOf(';')) : "";
		} else {
			logger.error(NO_VALID_REQUEST);
		}
		return null;
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
		} else if (Configuration.isJwtModeEnabled()) {
			try {
				userName = getContextProperty(DIRIGIBLE_JWT_USER);
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
