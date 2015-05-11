/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;

public class PathUtils {
	
	public static String extractPath(HttpServletRequest request)
			throws IllegalArgumentException {
		String requestPath = request.getPathInfo();
		if (requestPath == null) {
			requestPath = IRepository.SEPARATOR; //$NON-NLS-1$
		}
		return requestPath;
	}
	
	public static String getHeadingUrl(final HttpServletRequest req, String mapping) {
        final String scheme = req.getScheme() + "://"; //$NON-NLS-1$
        final String serverName = req.getServerName();
        final String serverPort = (req.getServerPort() == 80) ? "" : ":" //$NON-NLS-1$ //$NON-NLS-2$
                + req.getServerPort();
        final String contextPath = req.getContextPath();
        return scheme + serverName + serverPort + contextPath
                + mapping;
    }

}
