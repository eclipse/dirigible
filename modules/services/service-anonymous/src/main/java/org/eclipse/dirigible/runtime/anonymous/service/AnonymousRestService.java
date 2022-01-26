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
package org.eclipse.dirigible.runtime.anonymous.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.runtime.anonymous.AnonymousAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

/**
 * Front facing REST service serving the Security related content.
 */
@Path("/anonymous")
@Api(value = "Anonymous")
public class AnonymousRestService extends AbstractRestService implements IRestService {

	private static final String HOME_LOCATION = "../../home.html";

	private static final String ANONYMOUS_ACCESS_FAILED = "Anonymous access failed.";

	private static final Logger logger = LoggerFactory.getLogger(AnonymousRestService.class);

	private AnonymousAccess processor = new AnonymousAccess();

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
			return createErrorResponseInternalServerError(ANONYMOUS_ACCESS_FAILED);
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
