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
package org.eclipse.dirigible.core.publisher.api;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class PublisherHandler {

    protected final String REGISTRY_PUBLIC = "/registry/public/";

    private final String PERCENT = "%";

    private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);

    public abstract void beforePublish(String location) throws SchedulerException;

    public abstract void afterPublish(String location) throws SchedulerException;

    public abstract void beforeUnpublish(String location) throws SchedulerException;

    public abstract void afterUnpublish(String location) throws SchedulerException;

    protected String getLocationQueryParam(String location, boolean includeLeadingSeparator) {
        StringBuilder locationQueryParamSB = new StringBuilder();

        if (includeLeadingSeparator) {
            locationQueryParamSB.append(IRepositoryStructure.SEPARATOR);
        }

        locationQueryParamSB.append(location.replace(REGISTRY_PUBLIC, ""));
        locationQueryParamSB.append(IRepositoryStructure.SEPARATOR);
        locationQueryParamSB.append(PERCENT);

        return locationQueryParamSB.toString();
    }

    protected void removeMetadata(PersistenceManager persistenceManager, String table, String column, String locationQueryParam) throws SchedulerException {
        try (Connection connection = dataSource.getConnection()) {
            SqlFactory sqlFactory = SqlFactory.getNative(connection);

            if (sqlFactory.exists(connection, table)) {
                String sql = sqlFactory.delete().from(table).where(new StringBuilder().append(column).append(" LIKE ?").toString()).build();
                persistenceManager.execute(connection, sql, locationQueryParam);
            }
        } catch (SQLException e) {
            throw new SchedulerException(e);
        }
    }

}
