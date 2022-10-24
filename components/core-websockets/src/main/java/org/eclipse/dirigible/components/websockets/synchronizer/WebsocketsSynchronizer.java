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
package org.eclipse.dirigible.components.websockets.synchronizer;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.service.WebsocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Component
public class WebsocketsSynchronizer<A extends Artefact> implements Synchronizer<Websocket> {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketsSynchronizer.class);
    private static final String FILE_EXTENSION_WEBSOCKET = ".websocket";

    @Autowired
    private WebsocketService websocketService;

    private SynchronizerCallback callback;

    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(FILE_EXTENSION_WEBSOCKET);
    }

    @Override
    public boolean isAccepted(String type) {
        return Websocket.ARTEFACT_TYPE.equals(type);
    }

    @Override
    public List<Websocket> load(String location, byte[] content) {
        Websocket websocket = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), Websocket.class);
        websocket.setLocation(location);
        websocket.setType(Websocket.ARTEFACT_TYPE);
        websocket.updateKey();
        try {
            getService().save(websocket);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
                logger.error("websocket: {}", websocket);
                logger.error("content: {}", new String(content));
            }
        }
        return List.of(websocket);
    }

    @Override
    public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {

    }

    @Override
    public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
        try {
            List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ArtefactLifecycle.CREATED.toString());
            callback.registerErrors(this, results, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE_UPDATE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
        }
    }

    @Override
    public ArtefactService<Websocket> getService() {
        return websocketService;
    }

    @Override
    public void cleanup(Websocket websocket) {
        try {
            getService().delete(websocket);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
        }
    }

    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
        return true;
    }

    @Override
    public void setCallback(SynchronizerCallback callback) {
        this.callback = callback;
    }
}
