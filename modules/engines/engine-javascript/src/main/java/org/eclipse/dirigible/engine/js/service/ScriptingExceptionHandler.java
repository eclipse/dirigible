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
package org.eclipse.dirigible.engine.js.service;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ScriptingDependencyExceptionHandler.
 */
@Provider
public class ScriptingExceptionHandler extends AbstractExceptionHandler<ScriptingException> {

	private static final Logger logger = LoggerFactory.getLogger(ScriptingExceptionHandler.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getType()
	 */
	@Override
	public Class<? extends AbstractExceptionHandler<ScriptingException>> getType() {
		return ScriptingExceptionHandler.class;
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
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#logErrorMessage(org.slf4j.Logger, org.eclipse.dirigible.commons.api.scripting.ScriptingException)
	 */
	@Override
	protected void logErrorMessage(Logger logger, ScriptingException exception) {
		if (!exception.getCause().getClass().equals(RepositoryNotFoundException.class)) {
			super.logErrorMessage(logger, exception);
		}
		// Do nothing
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseStatus(java.lang.Throwable)
	 */
	@Override
	protected Status getResponseStatus(ScriptingException exception) {
		if (exception.getCause().getClass().equals(RepositoryNotFoundException.class)) {
			return Status.NOT_FOUND;
		}
		return Status.INTERNAL_SERVER_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseMessage(java.lang.Throwable)
	 */
	@Override
	protected String getResponseMessage(ScriptingException exception) {
		if (exception.getCause().getClass().equals(RepositoryNotFoundException.class)) {
			return exception.getCause().getMessage();
		}
		return exception.getMessage();
	}

}
