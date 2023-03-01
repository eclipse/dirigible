/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

/**
 * The AbstractRestService is the parent of all the RESTful services in Dirigible.
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
	 * Create general error response for given status.
	 *
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponse(Status status, String message) {
		return Response.serverError().status(status).entity(message).build();
	}

	/**
	 * Create error response not found.
	 *
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponseNotFound(String message) {
		return createErrorResponse(Status.NOT_FOUND, message);
	}

	/**
	 * Create error response forbidden.
	 *
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponseForbidden(String message) {
		return createErrorResponse(Status.FORBIDDEN, message);
	}

	/**
	 * Create error response bad request.
	 *
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponseBadRequest(String message) {
		return createErrorResponse(Status.BAD_REQUEST, message);
	}

	/**
	 * Create error response unathorized.
	 *
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponseUnauthorized(String message) {
		return createErrorResponse(Status.UNAUTHORIZED, message);
	}

	/**
	 * Create error response internal server error.
	 *
	 * @param message
	 *            the message
	 * @return the error response
	 */
	protected Response createErrorResponseInternalServerError(String message) {
		return createErrorResponse(Status.INTERNAL_SERVER_ERROR, message);
	}

}
