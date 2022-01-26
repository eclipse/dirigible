/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.core.filter;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;

import org.eclipse.dirigible.repository.api.IRepository;

/**
 * The Path Utils.
 */
public class PathUtils {

	/**
	 * Extract path.
	 *
	 * @param request
	 *            the request
	 * @return the string
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public static String extractPath(HttpServletRequest request) throws IllegalArgumentException {
		String requestPath = request.getPathInfo();
		if (requestPath == null) {
			requestPath = IRepository.SEPARATOR;
		}
		return requestPath;
	}

	/**
	 * Gets the heading url.
	 *
	 * @param req
	 *            the req
	 * @param mapping
	 *            the mapping
	 * @return the heading url
	 */
	public static String getHeadingUrl(final HttpServletRequest req, String mapping) {
		final String scheme = req.getScheme() + "://"; //$NON-NLS-1$
		final String serverName = req.getServerName();
		final String serverPort = (req.getServerPort() == 80) ? "" : ":" //$NON-NLS-1$ //$NON-NLS-2$
				+ req.getServerPort();
		final String contextPath = req.getContextPath();
		return scheme + serverName + serverPort + contextPath + mapping;
	}

	/**
	 * Extract path web socket.
	 *
	 * @param request
	 *            the request
	 * @return the string
	 */
	public static String extractPathWebSocket(HandshakeRequest request) {
		String requestPath = request.getRequestURI().toString();
		if ("".equals(requestPath)) {
			requestPath = IRepository.SEPARATOR;
		}
		return requestPath;
	}

}
