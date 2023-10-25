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
package org.eclipse.dirigible.components.data.anonymize.service;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.anonymize.domain.DataAnonymizeType;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.datafaker.Faker;

/**
 * The Class DataAnonymizeService.
 */
@Service
public class DataAnonymizeService {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DataAnonymizeService.class);

    /**
     * The data sources manager.
     */
    private final DataSourcesManager datasourceManager;
    
    /** The batch size. */
	private static int BATCH_SIZE = 1000;
	
	/** The Constant DIRIGIBLE_DATABASE_TRANSFER_BATCH_SIZE. */
	private static final String DIRIGIBLE_DATABASE_TRANSFER_BATCH_SIZE = "DIRIGIBLE_DATABASE_TRANSFER_BATCH_SIZE";
	
	/** The Constant DEFAULT_BATCH_SIZE. */
	private static final String DEFAULT_BATCH_SIZE = "1000";

    /**
     * Instantiates a new data source endpoint.
     *
     * @param datasourceManager        the datasource manager
     */
    @Autowired
    public DataAnonymizeService(DataSourcesManager datasourceManager) {
        this.datasourceManager = datasourceManager;
    }

    /**
     * Export structure.
     *
     * @param datasource the datasource
     * @param schema     the schema
     * @param table the table
     * @param column the column
     * @param primaryKey the primary key
     * @param type the type
     */
    public void anonymizeColumn(String datasource, String schema, String table, String column, String primaryKey, String type) {
        javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
        if (dataSource != null) {
        	
        	Faker faker = new Faker();
        	try {
    			BATCH_SIZE = Integer.parseInt(Configuration.get(DIRIGIBLE_DATABASE_TRANSFER_BATCH_SIZE, DEFAULT_BATCH_SIZE));
    		} catch (NumberFormatException e1) {
    			if (logger.isWarnEnabled()) {logger.warn("Wrong configuration for " + DIRIGIBLE_DATABASE_TRANSFER_BATCH_SIZE);}
    		}
        	
        	DataAnonymizeType typeValue = DataAnonymizeType.valueOf(type);
        	
            String tableName = "\"" + schema + "\".\"" + table + "\"";
            String columnName = "\"" + column + "\"";
            String primaryKeyName = "\"" + primaryKey + "\"";
			String select = "SELECT " + primaryKeyName + ", " + columnName + " FROM " + tableName;
			String update = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + primaryKeyName + " = ? ";
            try(Connection connection = dataSource.getConnection()) {
            	try (Statement statement = connection.createStatement()) {
            		int updatedRecords = 0;
            		try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
	            		ResultSet rs = statement.executeQuery(select);
	            		int size = rs.getMetaData().getColumnDisplaySize(2);
	            		while (rs.next()) {
            				int length = rs.getString(2).length();
	            			switch (typeValue) {
							case FULL_NAME: {
								preparedStatement.setString(1, truncate(faker.name().fullName(), size));
								break;
							}
							case FIRST_NAME: {
								preparedStatement.setString(1, truncate(faker.name().firstName(), size));
								break;
							}
							case LAST_NAME: {
								preparedStatement.setString(1, truncate(faker.name().lastName(), size));
								break;
							}
							case USER_NAME: {
								preparedStatement.setString(1, truncate(faker.internet().username(), size));
								break;
							}
							case EMAIL: {
								preparedStatement.setString(1, truncate((faker.internet().username() + "@acme.com"), size));
								break;
							}
							case PHONE: {
								preparedStatement.setString(1, faker.examplify(rs.getString(2)));
								break;
							}
							case ADDRESS: {
								preparedStatement.setString(1, truncate(faker.address().streetAddress(), size));
								break;
							}
							case CITY: {
								preparedStatement.setString(1, truncate(faker.address().city(), size));
								break;
							}
							case COUNTRY: {
								preparedStatement.setString(1, truncate(faker.address().country(), size));
								break;
							}
							case DATE: {
								Date date = rs.getDate(2);
								java.util.Date past = faker.date().past(10, TimeUnit.DAYS, new java.util.Date(date.getTime()));
								preparedStatement.setDate(1, new Date(past.getTime()));
								break;
							}
							case RANDOM: {
								preparedStatement.setString(1, faker.examplify(rs.getString(2)));
								break;
							}
							case MASK: {
								preparedStatement.setString(1, "*".repeat(length));
								break;
							}
							default:
								throw new IllegalArgumentException("Unexpected value: " + typeValue);
							}
	            			updatedRecords++;
	            				            			
	            			preparedStatement.setObject(2, rs.getObject(1));
	            			preparedStatement.addBatch();
							if (updatedRecords % BATCH_SIZE == 0) {
								preparedStatement.executeBatch();
							}
	            		}
	            		if (updatedRecords % BATCH_SIZE != 0) {
	            			preparedStatement.executeBatch();
						}
            		}
            	}
            } catch (Exception e) {
            	logger.error(e.getMessage(), e);
			}
        }
    }
    
    /**
     * Truncate.
     *
     * @param value the value
     * @param size the size
     * @return the string
     */
    String truncate(String value, int size) {
    	if (value != null) {
    		if (value.length() > size) {
    			return value.substring(0, size);
    		}
    	}
    	return value;
    }

}
