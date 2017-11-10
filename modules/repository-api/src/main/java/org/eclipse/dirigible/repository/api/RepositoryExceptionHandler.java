/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.api;

import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryExceptionHandler extends AbstractExceptionHandler<RepositoryException>{

	private static final Logger logger = LoggerFactory.getLogger(RepositoryExceptionHandler.class);

	@Override
	public Class<? extends AbstractExceptionHandler<RepositoryException>> getType() {
		return RepositoryExceptionHandler.class;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected Status getResponseStatus(RepositoryException exception) {
		return Status.INTERNAL_SERVER_ERROR;
	}

	@Override
	protected String getResponseMessage(RepositoryException exception) {
		return exception.getMessage();
	}

}
