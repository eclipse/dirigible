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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.eclipse.dirigible.commons.api.helpers.AppExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class AbstractExceptionHandler<T extends Throwable> implements ExceptionMapper<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractExceptionHandler.class);

	@Context
	private HttpServletResponse response;

	private static final Gson GSON = new Gson();

	@Override
	public Response toResponse(T exception) {
		getLogger().error(exception.getMessage(), exception);

		Status status = getResponseStatus(exception);
		String message = getResponseMessage(exception);
		AppExceptionMessage appException = new AppExceptionMessage(status, message);

		sendInternalServerError();
		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(GSON.toJson(appException)).build();
	}

	public abstract Class<? extends AbstractExceptionHandler<T>> getType();

	protected abstract Logger getLogger();

	protected abstract Status getResponseStatus(T exception);

	protected abstract String getResponseMessage(T exception);

	private void sendInternalServerError() {
		try {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
