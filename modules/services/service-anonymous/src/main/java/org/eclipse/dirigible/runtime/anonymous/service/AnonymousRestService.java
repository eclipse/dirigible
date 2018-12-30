/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.anonymous.service;

import java.io.IOException;
import java.rmi.AccessException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.runtime.anonymous.AnonymousAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

/**
 * Front facing REST service serving the Security related content.
 */
@Singleton
@Path("/anonymous")
@Api(value = "Anonymous")
public class AnonymousRestService extends AbstractRestService implements IRestService {

	private static final String HOME_LOCATION = "../../home.html";

	private static final String ANONYMOUS_ACCESS_FAILED = "Anonymous access failed.";

	private static final Logger logger = LoggerFactory.getLogger(AnonymousRestService.class);

	@Inject
	private AnonymousAccess processor;

	@Context
	private HttpServletResponse response;

	/**
	 * Set anonymous identifier.
	 *
	 * @return the response
	 * @throws IOException 
	 */
	@POST
	@Path("")
	public Response setIdentifier(@FormParam("identifier") String identifier) throws IOException {
		try {
			processor.setName(identifier);
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
			sendErrorInternalServerError(response, ANONYMOUS_ACCESS_FAILED);
			return Response.serverError().build();
		}
		response.sendRedirect(HOME_LOCATION);
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return AnonymousRestService.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}
}
