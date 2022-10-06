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
package org.eclipse.dirigible.components.configurations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("services/v4/core/configurations")
public class ConfigurationsController {
	
	
	private final ConfigurationsService configurationsService;
	
	@Autowired
	public ConfigurationsController(ConfigurationsService configurationsProcessor) {
		this.configurationsService = configurationsProcessor;
	}
	
	@ApiOperation(value = "", nickname = "getConfigurations", notes = "", response = List.class, responseContainer = "List", tags={ "Version", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Internal Server Error") })
    @GetMapping
    public ResponseEntity<List<List<String>>> getConfigurations() {
        return ResponseEntity.ok(configurationsService.getConfigurations());
    }

}
