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
package org.eclipse.dirigible.runtime.operations.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.NameValuePair;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.runtime.operations.processor.JobsProcessor;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Jobs.
 */
@Path("/ops/jobs")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Jobs", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class JobsService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(JobsService.class);

	private JobsProcessor processor = new JobsProcessor();
	
	@Context
	private HttpServletResponse response;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return JobsService.class;
	}

	/**
	 * List all the jobs currently registered.
	 *
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 */
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listJobs()
			throws SchedulerException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.list()).build();
	}
	
	/**
	 * Enable a job.
	 *
	 * @param name the job name
	 * @param request the request
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 */
	@POST
	@Path("enable/{name:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response enableJob(@PathParam("name") String name, @Context HttpServletRequest request)
			throws SchedulerException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.enable(IRepository.SEPARATOR + name)).build();
	}
	
	/**
	 * Disable a job.
	 *
	 * @param name the job name
	 * @param request the request
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 */
	@POST
	@Path("disable/{name:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response disableJob(@PathParam("name") String name, @Context HttpServletRequest request)
			throws SchedulerException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.disable(IRepository.SEPARATOR + name)).build();
	}
	
	/**
	 * Returns the job logs.
	 *
	 * @param name the job name
	 * @param request the request
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 */
	@GET
	@Path("logs/{name:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobLogs(@PathParam("name") String name, @Context HttpServletRequest request)
			throws SchedulerException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.logs(IRepository.SEPARATOR + name)).build();
	}
	
	/**
	 * Returns the job parameters.
	 *
	 * @param name the job name
	 * @param request the request
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 */
	@GET
	@Path("parameters/{name:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobParameters(@PathParam("name") String name, @Context HttpServletRequest request)
			throws SchedulerException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.parameters(IRepository.SEPARATOR + name)).build();
	}
	
	/**
	 * Triggers the job with parameters.
	 *
	 * @param name the job name
	 * @param parameters the job parameters
	 * @param request the request
	 * @return the response
	 * @throws SchedulerException the scheduler exception
	 * @throws JobExecutionException the execution exception
	 */
	@POST
	@Path("trigger/{name:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response triggerJob(@PathParam("name") String name, List<NameValuePair> parameters, @Context HttpServletRequest request)
			throws SchedulerException, JobExecutionException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.trigger(IRepository.SEPARATOR + name, parameters)).build();
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
