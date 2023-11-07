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
package org.eclipse.dirigible.components.odata.synchronizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.odata.domain.OData;
import org.eclipse.dirigible.components.odata.domain.ODataContainer;
import org.eclipse.dirigible.components.odata.domain.ODataHandler;
import org.eclipse.dirigible.components.odata.domain.ODataMapping;
import org.eclipse.dirigible.components.odata.domain.ODataSchema;
import org.eclipse.dirigible.components.odata.service.ODataContainerService;
import org.eclipse.dirigible.components.odata.service.ODataHandlerService;
import org.eclipse.dirigible.components.odata.service.ODataMappingService;
import org.eclipse.dirigible.components.odata.service.ODataSchemaService;
import org.eclipse.dirigible.components.odata.service.ODataService;
import org.eclipse.dirigible.components.odata.transformers.DefaultTableMetadataProvider;
import org.eclipse.dirigible.components.odata.transformers.OData2ODataHTransformer;
import org.eclipse.dirigible.components.odata.transformers.OData2ODataMTransformer;
import org.eclipse.dirigible.components.odata.transformers.OData2ODataXTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ListenerSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.ODATA)
public class ODataSynchronizer<A extends Artefact> implements Synchronizer<OData> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ODataSynchronizer.class);

    /** The Constant FILE_EXTENSION_LISTENER. */
    private static final String FILE_EXTENSION_ODATA = ".odata";

    /** The callback. */
    private SynchronizerCallback callback;

    /** The OData service. */
    @Autowired
    private ODataService odataService;

    /** The OData container service. */
    @Autowired
    private ODataContainerService odataContainerService;

    /** The OData handler service. */
    @Autowired
    private ODataHandlerService odataHandlerService;

    /** The OData mapping service. */
    @Autowired
    private ODataMappingService odataMappingService;

    /** The OData schema service. */
    @Autowired
    private ODataSchemaService odataSchemaService;

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
                   .endsWith(FILE_EXTENSION_ODATA);
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {
        return OData.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     * @throws ParseException
     */
    @Override
    public List<OData> parse(String location, byte[] content) throws ParseException {
        OData odata = parseOData(location, new String(content, StandardCharsets.UTF_8));
        try {
            OData maybe = getService().findByKey(odata.getKey());
            if (maybe != null) {
                odata.setId(maybe.getId());
            }
            odata = getService().save(odata);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("odata: {}", odata);
            }
            if (logger.isErrorEnabled()) {
                logger.error("content: {}", new String(content));
            }
            throw new ParseException(e.getMessage(), 0);
        }
        return List.of(odata);
    }

    /**
     * Retrieve.
     *
     * @param location the location
     * @return the list
     */
    @Override
    public List<OData> retrieve(String location) {
        List<OData> list = getService().getAll();
        for (OData odata : list) {
            OData parsed = parseOData(location, odata.getContent());
            odata.setEntities(parsed.getEntities());
            odata.setAssociations(parsed.getAssociations());
        }
        return list;
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
        getService().save((OData) artefact);
    }

    /**
     * Parses the O data.
     *
     * @param location the location
     * @param content the content
     * @return the o data
     */
    public static OData parseOData(String location, byte[] content) {
        return parseOData(location, new String(content, StandardCharsets.UTF_8));
    }

    /**
     * Parses the O data.
     *
     * @param location the location
     * @param content the content
     * @return the o data
     */
    public static OData parseOData(String location, String content) {
        OData odata = JsonHelper.fromJson(content, OData.class);
        Configuration.configureObject(odata);
        odata.setLocation(location);
        odata.setType(OData.ARTEFACT_TYPE);
        odata.setName(FilenameUtils.getBaseName(location));
        odata.setContent(content);
        odata.updateKey();
        odata.getAssociations()
             .forEach(association -> {
                 if (association.getFrom()
                                .getProperty() != null) {
                     association.getFrom()
                                .getProperties()
                                .add(association.getFrom()
                                                .getProperty());
                 }
                 if (association.getTo()
                                .getProperty() != null) {
                     association.getTo()
                                .getProperties()
                                .add(association.getTo()
                                                .getProperty());
                 }
             });
        return odata;
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<OData> getService() {
        return odataService;
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

        try {
            OData odata = null;
            if (wrapper.getArtefact() instanceof OData) {
                odata = (OData) wrapper.getArtefact();
            } else {
                throw new UnsupportedOperationException(String.format("Trying to process %s as OData", wrapper.getArtefact()
                                                                                                              .getClass()));
            }

            switch (flow) {
                case CREATE:
                    if (ArtefactLifecycle.NEW.equals(odata.getLifecycle())) {
                        generateOData(odata);
                        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                    }
                    break;
                case UPDATE:
                    if (ArtefactLifecycle.MODIFIED.equals(odata.getLifecycle())) {
                        cleanupOData(odata);
                        generateOData(odata);
                        callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
                    }
                    break;
                case DELETE:
                    if (ArtefactLifecycle.CREATED.equals(odata.getLifecycle()) || ArtefactLifecycle.UPDATED.equals(odata.getLifecycle())) {
                        cleanupOData(odata);
                        callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                    }
                    break;
            }
            return true;
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
            return false;
        }
    }

    /**
     * Generate O data.
     *
     * @param odata the odata
     * @throws SQLException the SQL exception
     */
    public void generateOData(OData odata) throws SQLException {
        // METADATA AND MAPPINGS GENERATION LOGIC
        String[] odataxc = generateODataSchema(odata);
        String odatax = odataxc[0];
        String odatac = odataxc[1];
        ODataSchema odataSchema = new ODataSchema(odata.getLocation(), odata.getName(), null, null, odatax.getBytes());
        odataSchemaService.save(odataSchema);
        ODataContainer odataContainer = new ODataContainer(odata.getLocation(), odata.getName(), null, null, odatac.getBytes());
        odataContainerService.save(odataContainer);

        String[] odatams = generateODataMappings(odata);
        int i = 1;
        for (String odatam : odatams) {
            ODataMapping odataMapping = new ODataMapping(odata.getLocation(), odata.getName() + "#" + i++, null, null, odatam.getBytes());
            odataMappingService.save(odataMapping);
        }

        List<ODataHandler> odatahs = generateODataHandlers(odata);
        for (ODataHandler odatah : odatahs) {
            ODataHandler odataHandler = new ODataHandler(odata.getLocation(), odatah.getName() + "#" + i++, null, null,
                    odatah.getNamespace(), odatah.getMethod(), odatah.getKind(), odatah.getHandler());
            odataHandlerService.save(odataHandler);
        }
    }

    /**
     * Cleanup O data.
     *
     * @param odata the odata
     */
    public void cleanupOData(OData odata) {
        // CLEAN UP LOGIC
        odataSchemaService.removeSchema(odata.getLocation());
        odataContainerService.removeContainer(odata.getLocation());
        odataMappingService.removeMappings(odata.getLocation());
        odataHandlerService.removeHandlers(odata.getLocation());
    }

    /**
     * Cleanup.
     *
     * @param odata the OData
     */
    @Override
    public void cleanup(OData odata) {
        try {
            odataSchemaService.removeSchema(odata.getLocation());
            odataContainerService.removeContainer(odata.getLocation());
            odataMappingService.removeMappings(odata.getLocation());
            odataHandlerService.removeHandlers(odata.getLocation());
            getService().delete(odata);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, odata, ArtefactLifecycle.DELETED, "");
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
        return FILE_EXTENSION_ODATA;
    }

    /**
     * Gets the artefact type.
     *
     * @return the artefact type
     */
    @Override
    public String getArtefactType() {
        return OData.ARTEFACT_TYPE;
    }

    /** The odata to odata mappings transformer. */
    private OData2ODataMTransformer odata2ODataMTransformer = new OData2ODataMTransformer();

    /** The odata to odata schema transformer. */
    private OData2ODataXTransformer odata2ODataXTransformer = new OData2ODataXTransformer(new DefaultTableMetadataProvider());

    /** The odata to odata handler transformer. */
    private OData2ODataHTransformer odata2ODataHTransformer = new OData2ODataHTransformer();

    /**
     * Generate OData Schema.
     *
     * @param model the model
     * @return the string[]
     * @throws SQLException the SQL exception
     */
    private String[] generateODataSchema(OData model) throws SQLException {
        return odata2ODataXTransformer.transform(model);
    }

    /**
     * Generate OData Mappings.
     *
     * @param model the model
     * @return the string[]
     * @throws SQLException the SQL exception
     */
    private String[] generateODataMappings(OData model) throws SQLException {
        return odata2ODataMTransformer.transform(model);
    }

    /**
     * Generate OData Handlers.
     *
     * @param model the model
     * @return the list
     * @throws SQLException the SQL exception
     */
    private List<ODataHandler> generateODataHandlers(OData model) throws SQLException {
        return odata2ODataHTransformer.transform(model);
    }

}
