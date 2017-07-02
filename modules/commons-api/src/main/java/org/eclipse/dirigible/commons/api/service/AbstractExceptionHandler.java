package org.eclipse.dirigible.commons.api.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.eclipse.dirigible.commons.api.helpers.AppExceptionMessage;
import org.slf4j.Logger;

import com.google.gson.Gson;

public abstract class AbstractExceptionHandler<T extends Throwable> implements ExceptionMapper<T> {

	private static final Gson GSON = new Gson();

	@Override
	public Response toResponse(T exception) {
		getLogger().error(exception.getMessage(), exception);

		Status status = getResponseStatus(exception);
		String message = getResponseMessage(exception);
		AppExceptionMessage appException = new AppExceptionMessage(status, message);

		return Response.status(status).type(MediaType.APPLICATION_JSON).entity(GSON.toJson(appException)).build();
	}

	public abstract Class<? extends AbstractExceptionHandler<T>> getType();

	protected abstract Logger getLogger();

	protected abstract Status getResponseStatus(T exception);
	
	protected abstract String getResponseMessage(T exception);
}
