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

/**
 * The Class MetadataPublisherHandler.
 */
public class MetadataPublisherHandler implements IPublisherHandler {

    /** The Constant REGISTRY_PUBLIC. */
    protected static final String REGISTRY_PUBLIC = IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR;

    /** The Constant PERCENT. */
    private static final String PERCENT = "%";

    /** The data source. */
    private DataSource dataSource = null;
    
    /**
     * Gets the data source.
     *
     * @return the data source
     */
    protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

    /**
     * Removes the metadata.
     *
     * @param persistenceManager the persistence manager
     * @param table the table
     * @param column the column
     * @param location the location
     * @param includeLeadingSeparator the include leading separator
     * @throws SchedulerException the scheduler exception
     */
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

    /**
     * Gets the location query param.
     *
     * @param location the location
     * @param includeLeadingSeparator the include leading separator
     * @return the location query param
     */
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

	/**
	 * Before publish.
	 *
	 * @param location the location
	 * @throws SchedulerException the scheduler exception
	 */
	@Override
	public void beforePublish(String location) throws SchedulerException {
		
	}

	/**
	 * After publish.
	 *
	 * @param workspaceLocation the workspace location
	 * @param registryLocation the registry location
	 * @throws SchedulerException the scheduler exception
	 */
	@Override
	public void afterPublish(String workspaceLocation, String registryLocation) throws SchedulerException {
		
	}

	/**
	 * Before unpublish.
	 *
	 * @param location the location
	 * @throws SchedulerException the scheduler exception
	 */
	@Override
	public void beforeUnpublish(String location) throws SchedulerException {
		
	}

	/**
	 * After unpublish.
	 *
	 * @param location the location
	 * @throws SchedulerException the scheduler exception
	 */
	@Override
	public void afterUnpublish(String location) throws SchedulerException {
		
	}

}
