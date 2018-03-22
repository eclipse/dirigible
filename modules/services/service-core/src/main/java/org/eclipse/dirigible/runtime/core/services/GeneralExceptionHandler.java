/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.core.services;

import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GeneralExceptionHandler.
 */
public class GeneralExceptionHandler extends AbstractExceptionHandler<Exception> {

	private static final Logger logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getType()
	 */
	@Override
	public Class<? extends AbstractExceptionHandler<Exception>> getType() {
		return GeneralExceptionHandler.class;
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
	protected Status getResponseStatus(Exception exception) {
		return Status.INTERNAL_SERVER_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler#getResponseMessage(java.lang.Throwable)
	 */
	@Override
	protected String getResponseMessage(Exception exception) {
		return exception.getMessage();
	}

}
