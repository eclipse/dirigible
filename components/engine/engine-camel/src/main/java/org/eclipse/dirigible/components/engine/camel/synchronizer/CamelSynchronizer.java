/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.synchronizer;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.eclipse.dirigible.components.engine.camel.service.CamelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Component
@Order(250)
public class CamelSynchronizer<A extends Artefact> implements Synchronizer<Camel> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CamelSynchronizer.class);

    /** The Constant FILE_EXTENSION_CAMEL. */
    public static final String FILE_EXTENSION_CAMEL = ".camel";

    /** The synchronization callback. */
    private SynchronizerCallback callback;

    /** The camel service. */
    private final CamelService camelService;
    private final CamelProcessor camelProcessor;

    /**
     * Instantiates a new camel synchronizer.
     *
     * @param camelService the camel service
     */
    @Autowired
    public CamelSynchronizer(CamelService camelService, CamelProcessor camelProcessor) {
        this.camelService = camelService;
        this.camelProcessor = camelProcessor;
    }

    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString()
                   .endsWith(getFileExtension());
    }

    @Override
    public boolean isAccepted(String type) {
        return Camel.ARTEFACT_TYPE.equals(type);
    }

    @Override
    public List<Camel> parse(String location, byte[] content) {
        Camel camel = new Camel();
        camel.setLocation(location);
        camel.setName(Paths.get(location)
                           .getFileName()
                           .toString());
        camel.setType(Camel.ARTEFACT_TYPE);
        camel.updateKey();
        camel.setContent(content);
        try {
            Camel maybe = getService().findByKey(camel.getKey());
            if (maybe != null) {
                camel.setId(maybe.getId());
            }
            getService().save(camel);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("camel: {}", camel);
            }
            if (logger.isErrorEnabled()) {
                logger.error("content: {}", new String(content));
            }
        }
        return List.of(camel);
    }

    @Override
    public List<Camel> retrieve(String location) {
        return getService().getAll();
    }

    @Override
    public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
        artefact.setLifecycle(lifecycle);
        artefact.setError(error);
        getService().save((Camel) artefact);
    }

    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        try {
            Camel camel = null;
            if (wrapper.getArtefact() instanceof Camel) {
                camel = (Camel) wrapper.getArtefact();
            } else {
                throw new UnsupportedOperationException(String.format("Trying to process %s as Camel", wrapper.getArtefact()
                                                                                                              .getClass()));
            }

            switch (flow) {
                case CREATE:
                    if (ArtefactLifecycle.NEW.equals(camel.getLifecycle())) {
                        addToProcessor(camel);
                        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                    }
                    break;
                case UPDATE:
                    if (ArtefactLifecycle.MODIFIED.equals(camel.getLifecycle())) {
                        addToProcessor(camel);
                        callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
                    }
                    break;
                case DELETE:
                    if (ArtefactLifecycle.CREATED.equals(camel.getLifecycle()) || ArtefactLifecycle.UPDATED.equals(camel.getLifecycle())) {
                        removeFromProcessor(camel);
                        callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                    }
                    break;
                case START: {
                    addToProcessor(camel);
                    callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                }
                    break;
                case STOP: {
                    removeFromProcessor(camel);
                    callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                }
                    break;
            }
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
            return false;
        }
    }

    @Override
    public ArtefactService<Camel> getService() {
        return camelService;
    }

    @Override
    public void cleanup(Camel camel) {
        try {
            removeFromProcessor(camel);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, camel, ArtefactLifecycle.DELETED, e.getMessage());
        }
    }

    @Override
    public void setCallback(SynchronizerCallback callback) {
        this.callback = callback;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION_CAMEL;
    }

    @Override
    public String getArtefactType() {
        return Camel.ARTEFACT_TYPE;
    }

    private void addToProcessor(Camel camel) {
        camelProcessor.onCreateOrUpdate(camel);
    }

    private void removeFromProcessor(Camel camel) {
        getService().delete(camel);
        camelProcessor.onRemove(camel);
    }
}
