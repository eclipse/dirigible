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
package org.eclipse.dirigible.engine.odata2.synchronizer;

import org.eclipse.dirigible.core.publisher.api.handlers.MetadataPublisherHandler;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.engine.odata2.definition.ODataContainerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;

/**
 * The Class ODataSynchronizerPublisherHandler.
 */
public class ODataSynchronizerPublisherHandler extends MetadataPublisherHandler {

    /**
     * After unpublish.
     *
     * @param location the location
     * @throws SchedulerException the scheduler exception
     */
    @Override
    public void afterUnpublish(String location) throws SchedulerException {
        removeMetadata(new PersistenceManager<ODataDefinition>(), "DIRIGIBLE_ODATA", "ODATA_LOCATION", location, true);
        removeMetadata(new PersistenceManager<ODataContainerDefinition>(), "DIRIGIBLE_ODATA_CONTAINER", "ODATAC_LOCATION", location, true);
        removeMetadata(new PersistenceManager<ODataHandlerDefinition>(), "DIRIGIBLE_ODATA_HANDLER", "ODATAH_LOCATION", location, true);
        removeMetadata(new PersistenceManager<ODataMappingDefinition>(), "DIRIGIBLE_ODATA_MAPPING", "ODATAM_LOCATION", location, true);
        removeMetadata(new PersistenceManager<ODataSchemaDefinition>(), "DIRIGIBLE_ODATA_SCHEMA", "ODATAX_LOCATION", location, true);
    }

}
