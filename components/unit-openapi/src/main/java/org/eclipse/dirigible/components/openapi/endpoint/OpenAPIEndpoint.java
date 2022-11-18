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

import com.fasterxml.jackson.core.JsonProcessingException;
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
 * The Class OpenAPIEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "openapi")
public class OpenAPIEndpoint extends BaseEndpoint {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(OpenAPIEndpoint.class);

    /** The Constant BASE_PATH. */
    private static final String BASE_PATH = "/services/v4";

    /** The Constant CONTACT_EMAIL. */
    private static final String CONTACT_EMAIL = "dirigible-dev@eclipse.org";

    /** The Constant DESCRIPTION. */
    private static final String DESCRIPTION = "Eclipse Dirigible API of the REST services provided by the applications";

    /** The Constant LICENSE_NAME. */
    private static final String LICENSE_NAME = "Eclipse Public License - v 2.0";

    /** The Constant LICENSE_URL. */
    private static final String LICENSE_URL = "https://www.eclipse.org/legal/epl-v20.html";

    /** The Constant TITLE. */
    private static final String TITLE = "Eclipse Dirigible - Applications REST Services API";

    /** The Constant VERSION. */
    private static final String VERSION = "6.0.0";

    /**
     * The openapi service.
     */
    @Autowired
    private OpenAPIService openAPIService;

    /**
     * The version service.
     */
    @Autowired
    private VersionService versionService;

    /**
     * Version.
     *
     * @return the response entity
     * @throws JsonProcessingException the json processing exception
     */
    @GetMapping
    public ResponseEntity<String> getVersion() throws JsonProcessingException {
        Contact contact = new Contact();
        contact.email(CONTACT_EMAIL);

        License license = new License();
        license.setName(LICENSE_NAME);
        license.setUrl(LICENSE_URL);

        Info info = new Info();
        info.setContact(contact);
        info.setDescription(DESCRIPTION);
        info.setLicense(license);
        //  info.setTermsOfService();
        info.setTitle(TITLE);

        try {
            Version version = versionService.getVersion();
            info.setVersion(version.getProductVersion());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            info.setVersion(VERSION);
        }

        Swagger swagger = initializeSwagger(info);

        for (OpenAPI openAPI : openAPIService.getAll()) {
            IResource resource = openAPIService.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + openAPI.getLocation());

            if (resource.exists()) {
                populateSwaggerFromContribution(swagger, resource);
            }
        }

        String swaggerJson = Json.mapper().writeValueAsString(swagger);
        return new ResponseEntity<>(swaggerJson, HttpStatus.OK);
    }

    /**
     * Initialize swagger.
     *
     * @param info the info
     * @return the swagger
     */
    private Swagger initializeSwagger(Info info) {
        Swagger swagger = new Swagger();
        swagger.basePath(BASE_PATH);
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
        return swagger;
    }

    /**
     * Populate swagger from contribution.
     *
     * @param swagger the swagger
     * @param resource the resource
     */
    private void populateSwaggerFromContribution(Swagger swagger, IResource resource) {
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
