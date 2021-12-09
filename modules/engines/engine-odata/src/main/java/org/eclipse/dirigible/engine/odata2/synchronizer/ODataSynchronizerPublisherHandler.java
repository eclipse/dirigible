/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.synchronizer;

import org.eclipse.dirigible.core.publisher.api.PublisherHandler;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.engine.odata2.definition.*;

public class ODataSynchronizerPublisherHandler extends PublisherHandler {

    @Override
    public void beforePublish(String location) {

    }

    @Override
    public void afterPublish(String location) {

    }

    @Override
    public void beforeUnpublish(String location) {

    }

    @Override
    public void afterUnpublish(String location) throws SchedulerException {
        String locationQueryParam = getLocationQueryParam(location, true);

        removeMetadata(new PersistenceManager<ODataDefinition>(), "DIRIGIBLE_ODATA", "ODATA_LOCATION", locationQueryParam);
        removeMetadata(new PersistenceManager<ODataContainerDefinition>(), "DIRIGIBLE_ODATA_CONTAINER", "ODATAC_LOCATION", locationQueryParam);
        removeMetadata(new PersistenceManager<ODataHandlerDefinition>(), "DIRIGIBLE_ODATA_HANDLER", "ODATAH_LOCATION", locationQueryParam);
        removeMetadata(new PersistenceManager<ODataMappingDefinition>(), "DIRIGIBLE_ODATA_MAPPING", "ODATAM_LOCATION", locationQueryParam);
        removeMetadata(new PersistenceManager<ODataSchemaDefinition>(), "DIRIGIBLE_ODATA_SCHEMA", "ODATAX_LOCATION", locationQueryParam);
    }

}
