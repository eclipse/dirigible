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

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.MultitenantBaseSynchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.eclipse.dirigible.components.data.csvim.processor.CsvimProcessor;
import org.eclipse.dirigible.components.data.csvim.service.CsvService;
import org.eclipse.dirigible.components.data.csvim.service.CsvimService;
import org.eclipse.dirigible.components.data.sources.config.SystemDataSourceName;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The Class CSVIM Synchronizer.
 */
@Component
@Order(SynchronizersOrder.CSVIM)
public class CsvimSynchronizer extends MultitenantBaseSynchronizer<Csvim, Long> {

    /**
     * The Constant FILE_EXTENSION_CSVIM.
     */
    public static final String FILE_EXTENSION_CSVIM = ".csvim";
    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvimSynchronizer.class);
    /**
     * The Constant CSVIM_SYNCHRONIZED.
     */
    private static final List<String> CSVIM_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<>());

    /**
     * The Constant CSV_SYNCHRONIZED.
     */
    private static final List<String> CSV_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<>());
    private final CsvimService csvimService;
    private final CsvService csvService;
    private final DataSourcesManager datasourcesManager;
    private final CsvimProcessor csvimProcessor;
    private final String systemDataSourceName;
    private SynchronizerCallback callback;

    /**
     * Instantiates a new csvim synchronizer.
     *
     * @param csvimService the csvimsyncrhonizer service
     * @param csvService the csvsyncrhonizer service
     * @param datasourcesManager the datasources manager
     * @param csvimProcessor the csvim processor
     * @param systemDataSourceName the system data source name
     */
    @Autowired
    public CsvimSynchronizer(CsvimService csvimService, CsvService csvService, DataSourcesManager datasourcesManager,
            CsvimProcessor csvimProcessor, @SystemDataSourceName String systemDataSourceName) {
        this.csvimService = csvimService;
        this.csvService = csvService;
        this.datasourcesManager = datasourcesManager;
        this.csvimProcessor = csvimProcessor;
        this.systemDataSourceName = systemDataSourceName;
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
            logger.error("Failed to save CSVIM [{}], content: [{}]", csvim, new String(content), e);
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Csvim, Long> getService() {
        return csvimService;
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
    public void setStatus(Csvim artefact, ArtefactLifecycle lifecycle, String error) {
        artefact.setLifecycle(lifecycle);
        artefact.setError(error);
        getService().save(artefact);
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    protected boolean completeImpl(TopologyWrapper<Csvim> wrapper, ArtefactPhase flow) {
        Csvim csvim = wrapper.getArtefact();
        try {
            switch (flow) {
                case CREATE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.NEW)) {
                        importCsvim(csvim);
                        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                    }
                    break;
                case UPDATE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.MODIFIED)) {
                        updateCsvim(csvim);
                        callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
                    }
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.FAILED)) {
                        return false;
                    }
                    break;
                case DELETE:
                    if (csvim.getLifecycle()
                             .equals(ArtefactLifecycle.CREATED)
                            || csvim.getLifecycle()
                                    .equals(ArtefactLifecycle.UPDATED)
                            || csvim.getLifecycle()
                                    .equals(ArtefactLifecycle.FAILED)) {
                        callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                    }
                    break;
                case START:
                case STOP:
            }

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
            return false;
        }
    }

    private void importCsvim(Csvim csvim) throws Exception {
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
                    if (!resource.exists()) {
                        throw new Exception("CSV does not exist: " + fileLocation);
                    }
                    content = csvimProcessor.getCsvContent(resource);
                    csv.setContent(content);
                    csv.setLocation(file.getLocation());
                    csv.setType(Csv.ARTEFACT_TYPE);
                    csv.setName(file.getName());
                    csv.updateKey();

                    csv = csvService.save(csv);
                    csvimProcessor.process(file, new ByteArrayInputStream(content), csvim.getDatasource());

                    csv.setImported(true);

                    csvService.save(csv);
                } catch (SQLException | IOException e) {
                    logger.error("An error occurred while trying to execute the data import of file [{}]", file, e);
                }
            }
        }
    }

    private void updateCsvim(Csvim csvim) throws Exception {
        List<CsvFile> files = csvim.getFiles();
        if (files != null) {
            for (CsvFile file : files) {
                try {
                    String fileLocation = file.getLocation();
                    byte[] content;
                    IResource resource = CsvimProcessor.getCsvResource(file);
                    if (!resource.exists()) {
                        throw new Exception("CSV does not exist: " + fileLocation);
                    }
                    content = csvimProcessor.getCsvContent(resource);
                    csvimProcessor.process(file, new ByteArrayInputStream(content), csvim.getDatasource());
                } catch (SQLException | IOException e) {
                    logger.error("An error occurred while trying to execute the data import of CSVIM [{}]", csvim, e);
                }
            }
        }
    }

    /**
     * Cleanup.
     *
     * @param csvim the csvim
     */
    @Override
    public void cleanupImpl(Csvim csvim) {
        try (Connection connection = datasourcesManager.getDefaultDataSource()
                                                       .getConnection()) {
            List<Csvim> csvims = csvimService.getAll();
            for (Csvim c : csvims) {
                if (!CSVIM_SYNCHRONIZED.contains(c.getLocation())) {
                    csvimService.delete(c);
                    logger.warn("Cleaned up CSVIM file from location: {}", c.getLocation());
                }
            }
            List<Csv> csvs = csvService.getAll();
            for (Csv csv : csvs) {
                if (!CSV_SYNCHRONIZED.contains(csv.getLocation())) {
                    csvService.delete(csv);
                    logger.warn("Cleaned up CSV file from location: {}", csv.getLocation());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to cleanup csvim [{}]", csvim, e);
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
     * Checks if is multitenant artefact.
     *
     * @param csvim the csvim
     * @return true, if is multitenant artefact
     */
    @Override
    protected boolean isMultitenantArtefact(Csvim csvim) {
        return !Objects.equals(systemDataSourceName, csvim.getDatasource());
    }
}
