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

package org.eclipse.dirigible.components.data.csvim.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.eclipse.dirigible.components.data.csvim.service.CsvService;
import org.eclipse.dirigible.components.data.csvim.service.CsvimService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class CSVIM Synchronizer.
 *
 * @param <A> the generic type
 */
@Component
public class CsvimSynchronizer<A extends Artefact> implements Synchronizer<Csvim> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvimSynchronizer.class);

    /**
     * The Constant FILE_EXTENSION_CSVIM.
     */
    public static final String FILE_EXTENSION_CSVIM = ".csvim";

    /** The Constant CSVIM_SYNCHRONIZED. */
    private static final List<String> CSVIM_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

    /** The Constant CSV_SYNCHRONIZED. */
    private static final List<String> CSV_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

    /**
     * The csvimsynchronizer service
     */
    private final CsvimService csvimService;

    /**
     * The csvimsynchronizer service
     */
    private final CsvService csvService;

    /**
     * The datasources manager.
     */
    private DataSourcesManager datasourcesManager;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new csvim synchronizer.
     *
     * @param csvimService       the csvimsyncrhonizer service
     * @param csvService         the csvsyncrhonizer service
     * @param datasourcesManager the datasources manager
     */

    @Autowired
    public CsvimSynchronizer(CsvimService csvimService, CsvService csvService, DataSourcesManager datasourcesManager) {
        this.csvimService = csvimService;
        this.csvService = csvService;
        this.datasourcesManager = datasourcesManager;
    }

    @Override
    public ArtefactService<Csvim> getService() {
        return csvimService;
    }

    /**
     * Checks if is accepted.
     *
     * @param file  the file
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
        return Csvim.ARTEFACT_TYPE.equals(type);
    }

    @Override
    public List<Csvim> parse(String location, byte[] content) {
        Csvim csvim = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Csvim.class);
        Configuration.configureObject(csvim);
        csvim.setLocation(location);
        csvim.setType(Csvim.ARTEFACT_TYPE);
        csvim.updateKey();
        if (csvim.getCsvFile() != null) {
            csvim.getCsvFile().forEach(cf -> cf.setCsvim(csvim));
        }

        try {
            Csvim maybe = getService().findByKey(csvim.getKey());
            if (maybe != null) {
                csvim.setId(maybe.getId());
                csvim.getCsvFile().forEach(cf -> {
                    CsvFile csvFile = maybe.getCsvFile(cf.getLocation());
                    if (csvFile != null) {
                        cf.setId(csvFile.getId());
                    }
                });
            }

            Csvim result = getService().save(csvim);
            return List.of(result);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("csvim: {}", csvim);
            }
            if (logger.isErrorEnabled()) {
                logger.error("content: {}", new String(content));
            }
        }

        return null;
    }

    @Override
    public List<Csvim> retrieve(String location) {
        return getService().getAll();
    }

    @Override
    public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
        artefact.setLifecycle(lifecycle);
        artefact.setError(error);
        getService().save((Csvim) artefact);
    }

    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        try (Connection connection = datasourcesManager.getDefaultDataSource().getConnection()){
            Csvim csvim = null;
            if (wrapper.getArtefact() instanceof Csvim){
                csvim = (Csvim) wrapper.getArtefact();
            } else {
                throw new UnsupportedOperationException(String.format("Trying to process %s as Csvim", wrapper.getArtefact().getClass()));
            }

            switch (flow){
                case CREATE:
                case UPDATE:
                case DELETE:
                case START:
                case STOP:
            }

            return true;
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
            return false;
        }
    }

    @Override
    public void cleanup(Csvim csvim) {
        try (Connection connection = datasourcesManager.getDefaultDataSource().getConnection()){
            List<Csvim> csvimDefinitions = csvimService.getAll();
            for (Csvim csvimDefinition : csvimDefinitions) {
                if (!CSVIM_SYNCHRONIZED.contains(csvimDefinition.getLocation())) {
                    csvimService.delete(csvimDefinition);
                    if (logger.isWarnEnabled()) {logger.warn("Cleaned up CSVIM file from location: {}", csvimDefinition.getLocation());}
                }
            }
            List<Csv> csvDefinitions = csvService.getAll();
            for (Csv csvDefinition : csvDefinitions) {
                if (!CSV_SYNCHRONIZED.contains(csvDefinition.getLocation())) {
                    csvService.delete(csvDefinition);
                    if (logger.isWarnEnabled()) {logger.warn("Cleaned up CSV file from location: {}", csvDefinition.getLocation());}
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, csvim, ArtefactLifecycle.DELETED, e.getMessage());
        }
    }

    @Override
    public void setCallback(SynchronizerCallback callback) {
        this.callback = callback;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION_CSVIM;
    }

    @Override
    public String getArtefactType() {
        return Csvim.ARTEFACT_TYPE;
    }
}