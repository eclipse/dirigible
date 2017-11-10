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
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.TestModeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);

	private static final String GUEST = "guest";

	private static volatile String TEST = "test";

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
		// TEST case
		if (Configuration.isTestModeEnabled()) {
			return TEST;
		}
		// Anonymous Case
		return GUEST;
	}

	public static final boolean isInRole(String role) {
		if (Configuration.isTestModeEnabled()) {
			return true;
		}
		try {
			return HttpRequestFacade.isUserInRole(role);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return false;
	}

	public static final void setName(String userName) throws TestModeException {
		if (Configuration.isTestModeEnabled()) {
			TEST = userName;
			logger.warn(format("User name set programmatically {0}", userName));
		} else {
			throw new TestModeException("Setting the user name programmatically is supported only when the test mode is enabled");
		}
	}

}
