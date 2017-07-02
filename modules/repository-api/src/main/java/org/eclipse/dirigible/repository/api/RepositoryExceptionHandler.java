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
