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

	private static final String DIRIGIBLE_ANONYMOUS_IDENTIFIER = "dirigible-anonymous-identifier";

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
		try {
			if (HttpRequestFacade.isValid()) {
				userName = HttpRequestFacade.getRemoteUser();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (userName != null) {
			return userName;
		}
		// Anonymous case
		if (Configuration.isAnonymousModeEnabled()) {
			if (HttpSessionFacade.isValid()) {
				userName = HttpSessionFacade.getAttribute(DIRIGIBLE_ANONYMOUS_IDENTIFIER);
			} else {
				if (ThreadContextFacade.isValid()) {
					try {
						Object value = ThreadContextFacade.get(DIRIGIBLE_ANONYMOUS_IDENTIFIER);
						if ((value != null) && (value instanceof String)) {
							userName = (String) value;
						}
					} catch (ContextException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
		if (userName != null) {
			return userName;
		}
		// Local Case
		return GUEST;
	}

	/**
	 * Checks if the user is in role.
	 *
	 * @param role
	 *            the role
	 * @return true, if the user is in role
	 */
	public static final boolean isInRole(String role) {
		if (Configuration.isAnonymousModeEnabled()) {
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
			if (HttpSessionFacade.isValid()) {
				HttpSessionFacade.setAttribute(DIRIGIBLE_ANONYMOUS_IDENTIFIER, userName);
				logger.warn(format("User name has been set programmatically {0} to the session as the anonymous mode is enabled", userName));
			} else {
				if (ThreadContextFacade.isValid()) {
					ThreadContextFacade.set(DIRIGIBLE_ANONYMOUS_IDENTIFIER, userName);
				}
			}
		} else {
			throw new SecurityException("Setting the user name programmatically is supported only when the anonymous mode is enabled");
		}
	}

}
