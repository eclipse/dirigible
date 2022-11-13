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
package org.eclipse.dirigible.components.openapi.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.service.OpenAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class OpenAPISynchronizer.
 *
 * @param A the generic type
 */
@Component
@Order(110)
public class OpenAPISynchronizer<A extends Artefact> implements Synchronizer<OpenAPI> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(OpenAPISynchronizer.class);

    /**
     * The Constant FILE_EXTENSION_OPENAPI.
     */
    private static final String FILE_EXTENSION_OPENAPI = ".openapi";

    /**
     * The openAPI service.
     */

    private OpenAPIService openAPIService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new openAPI synchronizer.
     *
     * @param openAPIService the openAPI service
     */
    @Autowired
    public OpenAPISynchronizer(OpenAPIService openAPIService) {
        this.openAPIService = openAPIService;
    }

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(getFileExtension());
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {
        return OpenAPI.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     */
    @Override
    public List<OpenAPI> load(String location, byte[] content) {
        OpenAPI openAPI = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), OpenAPI.class);
        openAPI.setLocation(location);
        openAPI.setName(FilenameUtils.getBaseName(location));
        openAPI.setType(OpenAPI.ARTEFACT_TYPE);
        openAPI.updateKey();
        try {
        	OpenAPI maybe = getService().findByKey(openAPI.getKey());
			if (maybe != null) {
				openAPI.setId(maybe.getId());
			}
            getService().save(openAPI);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("openAPI: {}", openAPI);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
        }
        return List.of(openAPI);
    }

    /**
     * Prepare.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {

    }

    /**
     * Process.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
        try {
            List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ArtefactLifecycle.CREATED.toString());
            callback.registerErrors(this, results, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE_UPDATE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
        }
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<OpenAPI> getService() {
        return openAPIService;
    }

    /**
     * Cleanup.
     *
     * @param openAPI the openAPI
     */
    @Override
    public void cleanup(OpenAPI openAPI) {
        try {
            getService().delete(openAPI);
            callback.registerState(this, openAPI, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, openAPI, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
        }
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
        return true;
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    @Override
    public void setCallback(SynchronizerCallback callback) {
        this.callback = callback;
    }
    
    @Override
	public String getFileExtension() {
		return FILE_EXTENSION_OPENAPI;
	}

	@Override
	public String getArtefactType() {
		return OpenAPI.ARTEFACT_TYPE;
	}
}
