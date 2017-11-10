/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractRestService.
 */
public abstract class AbstractRestService implements IRestService {

	/** The Constant NO_LOGGED_IN_USER. */
	public static final String NO_LOGGED_IN_USER = "No logged in user";

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	protected abstract Logger getLogger();

	/**
	 * Send error not found.
	 *
	 * @param response the response
	 * @param message the message
	 */
	protected void sendErrorNotFound(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Send error forbidden.
	 *
	 * @param response the response
	 * @param message the message
	 */
	protected void sendErrorForbidden(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Send error bad request.
	 *
	 * @param response the response
	 * @param message the message
	 */
	protected void sendErrorBadRequest(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Send error unathorized.
	 *
	 * @param response the response
	 * @param message the message
	 */
	protected void sendErrorUnathorized(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	/**
	 * Send error internal server error.
	 *
	 * @param response the response
	 * @param message the message
	 */
	protected void sendErrorInternalServerError(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

}
