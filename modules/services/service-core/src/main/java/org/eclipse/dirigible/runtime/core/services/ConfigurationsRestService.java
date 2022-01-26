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
package org.eclipse.dirigible.runtime.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front-facing service providing the version information
 *
 */
@Path("/core/configurations")
@RolesAllowed({ "Operator" })
@Api(value = "Version", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class ConfigurationsRestService implements IRestService {
	
	@Override
	public Class<? extends IRestService> getType() {
		return ConfigurationsRestService.class;
	}

	@GET
	@Path("")
	@Produces({ "application/json" })
	public List<List<String>> getConfigurations() {
		
		Map<String, String> runtimeVariables = Configuration.getRuntimeVariables();
		Map<String, String> environmentVariables = Configuration.getEnvironmentVariables();
		Map<String, String> deploymentVariables = Configuration.getDeploymentVariables();
		Map<String, String> moduleVariables = Configuration.getModuleVariables();
		
		List<List<String>> result = new ArrayList<List<String>>();
		for (String parameter : Configuration.getConfigurationParameters()) {
			List<String> row = new ArrayList<String>();
			row.add(parameter);
			row.add(runtimeVariables.get(parameter));
			row.add(environmentVariables.get(parameter));
			row.add(deploymentVariables.get(parameter));
			row.add(moduleVariables.get(parameter));
			result.add(row);
		}
		
		String customDataSourcesList = Configuration.get("DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES");
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				
				String parameter = name + "_DRIVER";
				List<String> row = new ArrayList<String>();
				row.add(parameter);
				row.add(runtimeVariables.get(parameter));
				row.add(environmentVariables.get(parameter));
				row.add(deploymentVariables.get(parameter));
				row.add(moduleVariables.get(parameter));
				result.add(row);
				
				parameter = name + "_URL";
				row = new ArrayList<String>();
				row.add(parameter);
				row.add(runtimeVariables.get(parameter));
				row.add(environmentVariables.get(parameter));
				row.add(deploymentVariables.get(parameter));
				row.add(moduleVariables.get(parameter));
				result.add(row);
				
				parameter = name + "_USERNAME";
				row = new ArrayList<String>();
				row.add(parameter);
				row.add(runtimeVariables.get(parameter));
				row.add(environmentVariables.get(parameter));
				row.add(deploymentVariables.get(parameter));
				row.add(moduleVariables.get(parameter));
				result.add(row);
				
				parameter = name + "_PASSWORD";
				row = new ArrayList<String>();
				row.add(parameter);
				row.add(runtimeVariables.get(parameter));
				row.add(environmentVariables.get(parameter));
				row.add(deploymentVariables.get(parameter));
				row.add(moduleVariables.get(parameter));
				result.add(row);
			}
		}
		
		return result;
	}
}
