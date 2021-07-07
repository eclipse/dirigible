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

/**
 * The AbstractExceptionHandler is the parent of the exception handlers in RESTful services.
 *
 * @param <T>
 *            the generic type
 */
public abstract class AbstractExceptionHandler<T extends Throwable> implements ExceptionMapper<T> {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractExceptionHandler.class);

	/** The response. */
	@Context
	private HttpServletResponse response;

	/** The Constant GSON. */
	private static final Gson GSON = new Gson();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(T exception) {
		logErrorMessage(getLogger(), exception);

		Status status = getResponseStatus(exception);
		String message = getResponseMessage(exception);
		AppExceptionMessage appException = new AppExceptionMessage(status, message);

		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(GSON.toJson(appException)).build();
	}

	protected void logErrorMessage(Logger logger, T exception) {
		logger.error(exception.getMessage(), exception);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract Class<? extends AbstractExceptionHandler<T>> getType();

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	protected abstract Logger getLogger();

	/**
	 * Gets the response status.
	 *
	 * @param exception
	 *            the exception
	 * @return the response status
	 */
	protected abstract Status getResponseStatus(T exception);

	/**
	 * Gets the response message.
	 *
	 * @param exception
	 *            the exception
	 * @return the response message
	 */
	protected abstract String getResponseMessage(T exception);

}
