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
package org.eclipse.dirigible.runtime.databases.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.database.transfer.api.DataTransferDefinition;
import org.eclipse.dirigible.database.transfer.api.DataTransferException;
import org.eclipse.dirigible.database.transfer.callbacks.WriterDataTransferCallbackHandler;
import org.eclipse.dirigible.runtime.databases.processor.DatabaseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw data.
 */
@Path("/ide/data/transfer")
@Api(value = "IDE - Data", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class DataTransferRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(DataTransferRestService.class);

	private DatabaseProcessor processor = new DatabaseProcessor();

	@Context
	private HttpServletResponse response;
	
	/**
	 * Request data transfer.
	 *
	 * @return the response
	 * @throws DataTransferException in case of error
	 * 
	 * Sample request:
	 * 
	 * {
	 *     "source": {
	 *         "type": "local",
	 *         "name": "SystemDB"
	 *     },
	 *     "target": {
	 *         "type": "defined",
	 *         "name": "MyDB"
	 *     },
 	 *    "configuration": {
 	 *        "sourceSchema": "PUBLIC",
	 *         "targetSchema": "PUBLIC"
 	 *    }
	 * }
	 */
	@GET
	@Path("")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation("Transfer data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request data transfer", response = String.class) })
	public Response createDefinedDatabases(@ApiParam(value = "Data transfer definition", required = true) DataTransferDefinition definition) throws DataTransferException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		if (definition == null) {
			return createErrorResponseBadRequest("Data transfer definition not provided");
		}
		
		StreamingOutput stream = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				Writer writer = new BufferedWriter(new OutputStreamWriter(output));
				WriterDataTransferCallbackHandler handler = new WriterDataTransferCallbackHandler(writer, user + new Date().getTime());
				try {
					processor.transferData(definition, handler);
				} catch (DataTransferException e) {
					throw new IOException(e);
				}
			}
		};
		
		return Response.ok(stream).build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return DataTransferRestService.class;
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
