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
package org.eclipse.dirigible.graalium.service;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.graalvm.polyglot.PolyglotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraaliumJavascriptExceptionHandler.
 */
@Provider
public class GraaliumJavascriptExceptionHandler extends AbstractExceptionHandler<PolyglotException> {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GraaliumJavascriptExceptionHandler.class);
	
	/** The Constant IGNORE_ERROR_MESSAGE. */
	private static final String IGNORE_ERROR_MESSAGE = "ReferenceError: \"exports\" is not defined";
	
	/** The Constant RESPONSE_ERROR_MESSAGE. */
	private static final String RESPONSE_ERROR_MESSAGE = "It is not an executable JavaScript module";
	
	/** The Constant NULL_EXCEPTION_ERROR_MESSAGE. */
	private static final String NULL_EXCEPTION_ERROR_MESSAGE = "Exception message is null";

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getType()
	 */
	@Override
	public Class<? extends AbstractExceptionHandler<PolyglotException>> getType() {
		return GraaliumJavascriptExceptionHandler.class;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * Gets the response status.
	 *
	 * @param exception the exception
	 * @return the response status
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseStatus(java.lang.Throwable)
	 */
	@Override
	protected Status getResponseStatus(PolyglotException exception) {
		return Status.INTERNAL_SERVER_ERROR;
	}

	/**
	 * Gets the response message.
	 *
	 * @param exception the exception
	 * @return the response message
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseMessage(java.lang.Throwable)
	 */
	@Override
	protected String getResponseMessage(PolyglotException exception) {
		if (exception.getMessage() == null) {
			return NULL_EXCEPTION_ERROR_MESSAGE;
		} else if (!exception.getMessage().equals(IGNORE_ERROR_MESSAGE)) {
			return exception.getMessage();
		}
		return RESPONSE_ERROR_MESSAGE;
	}

	/**
	 * Log error message.
	 *
	 * @param logger the logger
	 * @param exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#logErrorMessage()
	 */
	@Override
	protected void logErrorMessage(Logger logger, PolyglotException exception) {
		if (exception.getMessage() == null || !exception.getMessage().equals(IGNORE_ERROR_MESSAGE)) {
			super.logErrorMessage(logger, exception);
		}
	}
}
