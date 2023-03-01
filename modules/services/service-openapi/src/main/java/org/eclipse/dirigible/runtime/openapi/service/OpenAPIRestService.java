/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.openapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.core.version.Version;
import org.eclipse.dirigible.runtime.core.version.VersionProcessor;
import org.eclipse.dirigible.runtime.openapi.definition.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Model;
import io.swagger.models.Scheme;
import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;

/**
 * OpenAPI descriptor generation service.
 */
@Path("/openapi")
@RolesAllowed({ "Developer", "Operator" })
@Api(value = "OpenAPI", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class OpenAPIRestService extends AbstractRestService implements IRestService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OpenAPIRestService.class);
	
	/** The open API core service. */
	private OpenAPICoreService openAPICoreService = new OpenAPICoreService();
	
	/** The repository. */
	private IRepository repository = null;
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}
	
	/** The response. */
	@Context
	private HttpServletResponse response;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return OpenAPIRestService.class;
	}
	
	/**
	 * Get the version  information.
	 *
	 * @return the response
	 * @throws Exception the scheduler exception
	 */
	@GET
	@Path("")
	@Produces({ "application/json" })
	public Response version() throws Exception {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		Swagger swagger = new Swagger();
		swagger.basePath("/services/v4");
		Info info = new Info();
		Contact contact = new Contact();
		contact.email("dirigible-dev@eclipse.org");
		info.setContact(contact);
		info.setDescription("Eclipse Dirigible API of the REST services provided by the applications");
		License license = new License();
		license.setName("Eclipse Public License - v 2.0");
		license.setUrl("https://www.eclipse.org/legal/epl-v20.html");
		info.setLicense(license);
//		info.setTermsOfService();
		info.setTitle("Eclipse Dirigible - Applications REST Services API");
		
		try {
			Version version = new VersionProcessor().getVersion();
			info.setVersion(version.getProductVersion());
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			info.setVersion("6.0.0");
		}
		swagger.info(info);
		
		swagger.setConsumes(new ArrayList<String>());
		swagger.setDefinitions(new HashMap<String, Model>());
		swagger.setParameters(new HashMap<String, Parameter>());
		swagger.setPaths(new HashMap<String, io.swagger.models.Path>());
		swagger.setProduces(new ArrayList<String>());
		swagger.setResponses(new HashMap<String, io.swagger.models.Response>());
		swagger.setSchemes(new ArrayList<Scheme>());
		swagger.setSecurity(new ArrayList<SecurityRequirement>());
		swagger.setSecurityDefinitions(new HashMap<String, SecuritySchemeDefinition>());
		swagger.setTags(new ArrayList<Tag>());
		
		for (OpenAPIDefinition openAPIDefinition : openAPICoreService.getOpenAPIs()) {
			IResource resource = getRepository().getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + openAPIDefinition.getLocation());
			if (resource.exists()) {
				String service = new String(resource.getContent());
				Swagger contribution = new SwaggerParser().parse(service);
				if (contribution != null) {
					if (contribution.getConsumes() != null) {
						swagger.getConsumes().addAll(contribution.getConsumes());
					}
					if (contribution.getDefinitions() != null) {
						swagger.getDefinitions().putAll(contribution.getDefinitions());
					}
					if (contribution.getParameters() != null) {
						swagger.getParameters().putAll(contribution.getParameters());
					}
					if (contribution.getPaths() != null) {
						swagger.getPaths().putAll(contribution.getPaths());
					}
					if (contribution.getProduces() != null) {
						swagger.getProduces().addAll(contribution.getProduces());
					}
					if (contribution.getResponses() != null) {
						swagger.getResponses().putAll(contribution.getResponses());
					}
					if (contribution.getSchemes() != null) {
						swagger.getSchemes().addAll(contribution.getSchemes());
					}
					if (contribution.getSecurity() != null) {
						swagger.getSecurity().addAll(contribution.getSecurity());
					}
					if (contribution.getSecurityDefinitions() != null) {
						swagger.getSecurityDefinitions().putAll(contribution.getSecurityDefinitions());
					}
					if (contribution.getTags() != null) {
						swagger.getTags().addAll(contribution.getTags());
					}
				}
				
			}
		}
		String swaggerJson = Json.mapper().writeValueAsString(swagger);
		return Response.ok().entity(swaggerJson).build();
	}
	
	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
