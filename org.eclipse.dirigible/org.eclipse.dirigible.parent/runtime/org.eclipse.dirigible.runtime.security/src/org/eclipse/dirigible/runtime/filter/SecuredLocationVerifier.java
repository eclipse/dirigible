/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.filter;

import javax.servlet.ServletException;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.security.SecuritySynchronizer;

/**
 * Utility class that checks whether the location is secured via the *.access file
 */
public class SecuredLocationVerifier {

	private static final Logger logger = Logger.getLogger(SecuredLocationVerifier.class);

	/**
	 * Checks whether the location is secured via the *.access file or not
	 *
	 * @param location
	 * @return
	 * @throws ServletException
	 */
	public static String isLocationSecured(String location) throws ServletException {

		for (String securedLocation : SecuritySynchronizer.getSecuredLocations()) {
			if (location.startsWith(securedLocation)) {
				logger.debug(String.format("Location: %s is secured because of definition: %s", location, securedLocation));
				return securedLocation;
			}
		}
		logger.debug(String.format("Location: %s is NOT secured", location));
		return null;
	}

}
