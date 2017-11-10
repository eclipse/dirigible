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

public abstract class AbstractRestService implements IRestService {

	public static final String NO_LOGGED_IN_USER = "No logged in user";

	protected abstract Logger getLogger();

	protected void sendErrorNotFound(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	protected void sendErrorForbidden(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	protected void sendErrorBadRequest(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	protected void sendErrorUnathorized(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	protected void sendErrorInternalServerError(HttpServletResponse response, String message) {
		try {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

}
