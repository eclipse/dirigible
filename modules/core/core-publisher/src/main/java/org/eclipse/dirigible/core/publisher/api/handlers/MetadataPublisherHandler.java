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
package org.eclipse.dirigible.core.publisher.api.handlers;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.publisher.api.IPublisherHandler;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MetadataPublisherHandler implements IPublisherHandler {

    protected static final String REGISTRY_PUBLIC = IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR;

    private static final String PERCENT = "%";

    private DataSource dataSource = null;
    
    protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

    protected void removeMetadata(PersistenceManager persistenceManager, String table, String column, String location, boolean includeLeadingSeparator) throws SchedulerException {
        try (Connection connection = getDataSource().getConnection()) {
            SqlFactory sqlFactory = SqlFactory.getNative(connection);

            if (sqlFactory.exists(connection, table)) {
                String sql = sqlFactory.delete().from(table).where(new StringBuilder().append(column).append(" LIKE ?").toString()).build();
                String locationQueryParam = getLocationQueryParam(location, includeLeadingSeparator);

                persistenceManager.execute(connection, sql, locationQueryParam);
            }
        } catch (SQLException e) {
            throw new SchedulerException(e);
        }
    }

    private String getLocationQueryParam(String location, boolean includeLeadingSeparator) {
        StringBuilder locationQueryParamSB = new StringBuilder();

        if (includeLeadingSeparator) {
            locationQueryParamSB.append(IRepositoryStructure.SEPARATOR);
        }

        locationQueryParamSB.append(location.replace(REGISTRY_PUBLIC, ""));
        locationQueryParamSB.append(IRepositoryStructure.SEPARATOR);
        locationQueryParamSB.append(PERCENT);

        return locationQueryParamSB.toString();
    }

	@Override
	public void beforePublish(String location) throws SchedulerException {
		
	}

	@Override
	public void afterPublish(String workspaceLocation, String registryLocation) throws SchedulerException {
		
	}

	@Override
	public void beforeUnpublish(String location) throws SchedulerException {
		
	}

	@Override
	public void afterUnpublish(String location) throws SchedulerException {
		
	}

}
