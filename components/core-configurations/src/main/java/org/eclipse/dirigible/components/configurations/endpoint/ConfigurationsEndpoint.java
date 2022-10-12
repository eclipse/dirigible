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
package org.eclipse.dirigible.components.configurations.endpoint;

import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.configurations.service.ConfigurationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "configurations")
public class ConfigurationsEndpoint extends BaseEndpoint {
	
	
	private final ConfigurationsService configurationsService;
	
	@Autowired
	public ConfigurationsEndpoint(ConfigurationsService configurationsService) {
		this.configurationsService = configurationsService;
	}
	
    @GetMapping
    public ResponseEntity<List<List<String>>> findAll() {
        return ResponseEntity.ok(configurationsService.findAll());
    }

}
