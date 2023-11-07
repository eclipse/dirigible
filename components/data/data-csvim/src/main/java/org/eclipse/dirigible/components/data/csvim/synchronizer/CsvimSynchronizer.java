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

package org.eclipse.dirigible.components.data.csvim.synchronizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.eclipse.dirigible.components.data.csvim.service.CsvService;
import org.eclipse.dirigible.components.data.csvim.service.CsvimService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class CSVIM Synchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.CSVIM)
public class CsvimSynchronizer<A extends Artefact> implements Synchronizer<Csvim> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvimSynchronizer.class);

    /**
     * The Constant FILE_EXTENSION_CSVIM.
     */
    public static final String FILE_EXTENSION_CSVIM = ".csvim";

    /**
     * The Constant CSVIM_SYNCHRONIZED.
     */
    private static final List<String> CSVIM_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

    /**
     * The Constant CSV_SYNCHRONIZED.
     */
    private static final List<String> CSV_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

    /** The csvimsynchronizer service. */
    private final CsvimService csvimService;

    /** The csvimsynchronizer service. */
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
     * The csvim processor.
     */
    private CsvimProcessor csvimProcessor;

    /**
     * Instantiates a new csvim synchronizer.
     *
     * @param csvimService the csvimsyncrhonizer service
     * @param csvService the csvsyncrhonizer service
     * @param datasourcesManager the datasources manager
     * @param csvimProcessor the csvim processor
     */
    @Autowired
    public CsvimSynchronizer(CsvimService csvimService, CsvService csvService, DataSourcesManager datasourcesManager,
            CsvimProcessor csvimProcessor) {
        this.csvimService = csvimService;
        this.csvService = csvService;
        this.datasourcesManager = datasourcesManager;
        this.csvimProcessor = csvimProcessor;
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Csvim> getService() {
        return csvimService;
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
        return file.toString()
                   .endsWith(getFileExtension());
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

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the csvim
     * @throws ParseException the parse exception
     */
    @Override
    public List<Csvim> parse(String location, byte[] content) throws ParseException {
        Csvim csvim = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Csvim.class);
        Configuration.configureObject(csvim);
        csvim.setLocation(location);
        csvim.setName(FilenameUtils.getBaseName(location));
        csvim.setType(Csvim.ARTEFACT_TYPE);
        csvim.updateKey();
        if (csvim.getFiles() != null) {
            csvim.getFiles()
                 .forEach(cf -> {
                     cf.setCsvim(csvim);
                     cf.setLocation(csvim.getLocation() + "/" + cf.getFile());
                     cf.setType(CsvFile.ARTEFACT_TYPE);
                     cf.setName(cf.getFile());
                     cf.updateKey();
                 });
        }

        try {
            Csvim maybe = getService().findByKey(csvim.getKey());
            if (maybe != null) {
                csvim.setId(maybe.getId());
                csvim.getFiles()
                     .forEach(cf -> {
                         CsvFile csvFile = maybe.getFileByLocation(cf.getLocation());
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
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Retrieve.
     *
     * @param location the location
     * @return the csvim
     */
    @Override
    public List<Csvim> retrieve(String location) {
        return getService().getAll();
    }

    /**
     * Sets the status.
     *
     * @param artefact the artefact
     * @param lifecycle the lifecycle
     * @param error the error
     */
    @Override
    public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
        artefact.setLifecycle(lifecycle);
        artefact.setError(error);
        getService().save((Csvim) artefact);
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        try (Connection connection = datasourcesManager.getDefaultDataSource()
                                                       .getConnection()) {
            Csvim csvim;
            if (wrapper.getArtefact() instanceof Csvim) {
                csvim = (Csvim) wrapper.getArtefact();
            } else {
                throw new UnsupportedOperationException(String.format("Trying to process %s as Csvim", wrapper.getArtefact()
                                                                                                              .getClass()));
            }

            switch (flow) {
                case CREATE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.NEW)) {
                        importCsvim(csvim, connection);
                        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                    }
                    break;
                case UPDATE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.MODIFIED)) {
                        updateCsvim(csvim, connection);
                        callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
                    }
                    break;
                case DELETE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.CREATED)
                            || csvim.getLifecycle()
                                    .equals(ArtefactLifecycle.UPDATED)) {
                        callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                    }
                    break;
                case START:
                case STOP:
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

    /**
     * Cleanup.
     *
     * @param csvim the csvim
     */
    @Override
    public void cleanup(Csvim csvim) {
        try (Connection connection = datasourcesManager.getDefaultDataSource()
                                                       .getConnection()) {
            List<Csvim> csvims = csvimService.getAll();
            for (Csvim c : csvims) {
                if (!CSVIM_SYNCHRONIZED.contains(c.getLocation())) {
                    csvimService.delete(c);
                    if (logger.isWarnEnabled()) {
                        logger.warn("Cleaned up CSVIM file from location: {}", c.getLocation());
                    }
                }
            }
            List<Csv> csvs = csvService.getAll();
            for (Csv csv : csvs) {
                if (!CSV_SYNCHRONIZED.contains(csv.getLocation())) {
                    csvService.delete(csv);
                    if (logger.isWarnEnabled()) {
                        logger.warn("Cleaned up CSV file from location: {}", csv.getLocation());
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, csvim, ArtefactLifecycle.DELETED, e.getMessage());
        }
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

    /**
     * Gets the file extension.
     *
     * @return the file extension
     */
    @Override
    public String getFileExtension() {
        return FILE_EXTENSION_CSVIM;
    }

    /**
     * Gets the artefact type.
     *
     * @return the artefact type
     */
    @Override
    public String getArtefactType() {
        return Csvim.ARTEFACT_TYPE;
    }


    /**
     * Execute csvim.
     *
     * @param csvim the csvim
     * @param connection the connection
     * @throws Exception the exception
     */
    private void importCsvim(Csvim csvim, Connection connection) throws Exception {
        List<CsvFile> files = csvim.getFiles();

        if (files != null) {
            for (CsvFile file : files) {
                try {
                    Csv csv;
                    String fileLocation = file.getLocation();
                    List<Csv> list = csvService.findByLocation(fileLocation);
                    if (list.size() > 0) {
                        csv = list.get(0);
                    } else {
                        csv = new Csv();
                    }
                    byte[] content;
                    IResource resource = CsvimProcessor.getCsvResource(file);
                    if (resource.exists()) {
                        content = csvimProcessor.getCsvContent(resource);
                    } else {
                        throw new Exception("CSV does not exist: " + fileLocation);
                    }
                    csv.setContent(content);
                    csv.setLocation(file.getLocation());
                    csv.setType(Csv.ARTEFACT_TYPE);
                    csv.setName(file.getName());
                    csv.updateKey();

                    csv = csvService.save(csv);
                    csvimProcessor.process(file, new ByteArrayInputStream(content), connection);

                    csv.setImported(true);

                    csvService.save(csv);
                } catch (SQLException | IOException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("An error occurred while trying to execute the data import: %s", e.getMessage()), e);
                    }
                }
            }
        }
    }

    /**
     * Update csvim.
     *
     * @param csvim the csvim
     * @param connection the connection
     * @throws Exception the exception
     */

    private void updateCsvim(Csvim csvim, Connection connection) throws Exception {
        List<CsvFile> files = csvim.getFiles();
        if (files != null) {
            for (CsvFile file : files) {
                try {
                    String fileLocation = file.getLocation();
                    byte[] content;
                    IResource resource = CsvimProcessor.getCsvResource(file);
                    if (resource.exists()) {
                        content = csvimProcessor.getCsvContent(resource);
                    } else {
                        throw new Exception("CSV does not exist: " + fileLocation);
                    }
                    csvimProcessor.process(file, new ByteArrayInputStream(content), connection);
                } catch (SQLException | IOException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("An error occurred while trying to execute the data import: %s", e.getMessage()), e);
                    }
                }
            }
        }
    }
}
