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
package org.eclipse.dirigible.engine.js.graalvm.service;

import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.graalvm.polyglot.PolyglotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraalVMJavascriptExceptionHandler.
 */
public class GraalVMJavascriptExceptionHandler extends AbstractExceptionHandler<PolyglotException> {

	private static final Logger logger = LoggerFactory.getLogger(GraalVMJavascriptExceptionHandler.class);
	
	private static final String IGNORE_ERROR_MESSAGE = "ReferenceError: \"exports\" is not defined";
	private static final String RESPONSE_ERROR_MESSAGE = "It is not an executable JavaScript module";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getType()
	 */
	@Override
	public Class<? extends AbstractExceptionHandler<PolyglotException>> getType() {
		return GraalVMJavascriptExceptionHandler.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseStatus(java.lang.Throwable)
	 */
	@Override
	protected Status getResponseStatus(PolyglotException exception) {
		return Status.INTERNAL_SERVER_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseMessage(java.lang.Throwable)
	 */
	@Override
	protected String getResponseMessage(PolyglotException exception) {
		if (!exception.getMessage().equals(IGNORE_ERROR_MESSAGE)) {
			return exception.getMessage();
		}
		return RESPONSE_ERROR_MESSAGE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#logErrorMessage()
	 */
	@Override
	protected void logErrorMessage(Logger logger, PolyglotException exception) {
		if (!exception.getMessage().equals(IGNORE_ERROR_MESSAGE)) {
			super.logErrorMessage(logger, exception);
		}
		
	}
}
