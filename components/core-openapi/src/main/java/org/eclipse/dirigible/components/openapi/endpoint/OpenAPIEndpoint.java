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
package org.eclipse.dirigible.components.openapi.endpoint;

import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.service.OpenAPIService;
import org.eclipse.dirigible.components.version.domain.Version;
import org.eclipse.dirigible.components.version.service.VersionService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * OpenAPI descriptor generation service.
 */

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "openapi")
public class OpenAPIEndpoint extends BaseEndpoint {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(OpenAPIEndpoint.class);

    /**
     * The open API core service.
     */
    @Autowired
    private OpenAPIService openAPIService;

    @Autowired
    private VersionService versionService;

    @GetMapping
    public ResponseEntity<String> version() throws Exception {
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
            Version version = versionService.getVersion();
            info.setVersion(version.getProductVersion());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            info.setVersion("6.0.0");
        }

        swagger.info(info);

        swagger.setConsumes(new ArrayList<>());
        swagger.setDefinitions(new HashMap<>());
        swagger.setParameters(new HashMap<>());
        swagger.setPaths(new HashMap<>());
        swagger.setProduces(new ArrayList<>());
        swagger.setResponses(new HashMap<>());
        swagger.setSchemes(new ArrayList<>());
        swagger.setSecurity(new ArrayList<>());
        swagger.setSecurityDefinitions(new HashMap<>());
        swagger.setTags(new ArrayList<>());

        for (OpenAPI openAPI : openAPIService.getAll()) {
            IResource resource = openAPIService.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + openAPI.getLocation());
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
        return new ResponseEntity<>(swaggerJson, HttpStatus.OK);
    }
}
