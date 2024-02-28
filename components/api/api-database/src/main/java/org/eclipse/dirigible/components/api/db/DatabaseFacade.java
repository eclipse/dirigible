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
package org.eclipse.dirigible.components.api.db;

import static java.text.MessageFormat.format;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.io.output.WriterOutputStream;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseResultSetHelper;
import org.eclipse.dirigible.components.data.management.service.DatabaseDefinitionService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.database.persistence.processors.identity.PersistenceNextValueIdentityProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class DatabaseFacade.
 */
@Component
public class DatabaseFacade implements InitializingBean {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFacade.class);

    /** The database facade. */
    private static DatabaseFacade INSTANCE;

    /** The database definition service. */
    private final DatabaseDefinitionService databaseDefinitionService;

    /** The data sources manager. */
    private final DataSourcesManager dataSourcesManager;

    /**
     * Instantiates a new database facade.
     *
     * @param databaseDefinitionService the database definition service
     * @param dataSourcesManager the data sources manager
     */
    @Autowired
    private DatabaseFacade(DatabaseDefinitionService databaseDefinitionService, DataSourcesManager dataSourcesManager) {
        this.databaseDefinitionService = databaseDefinitionService;
        this.dataSourcesManager = dataSourcesManager;
    }

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    /**
     * Gets the instance.
     *
     * @return the database facade
     */
    public static DatabaseFacade get() {
        return INSTANCE;
    }

    /**
     * Gets the database definition service.
     *
     * @return the database definition service
     */
    public DatabaseDefinitionService getDatabaseDefinitionService() {
        return databaseDefinitionService;
    }

    /**
     * Gets the data sources manager.
     *
     * @return the data sources manager
     */
    public DataSourcesManager getDataSourcesManager() {
        return dataSourcesManager;
    }

    /**
     * Gets the data sources.
     *
     * @return the data sources
     */
    public static final String getDataSources() {
        return GsonHelper.toJson(DatabaseFacade.get()
                                               .getDatabaseDefinitionService()
                                               .getDataSourcesNames());
    }

    /**
     * Gets the default data source.
     *
     * @return the default data source
     */
    public static final DataSource getDefaultDataSource() {
        return DatabaseFacade.get()
                             .getDataSourcesManager()
                             .getDefaultDataSource();
    }

    /**
     * Gets the metadata.
     *
     * @param datasourceName the datasource name
     * @return the metadata
     * @throws SQLException the SQL exception
     */
    public static final String getMetadata(String datasourceName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        return DatabaseMetadataHelper.getMetadataAsJson(dataSource);
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     * @throws SQLException the SQL exception
     */
    public static final String getMetadata() throws SQLException {
        DataSource dataSource = getDataSource(null);
        if (dataSource == null) {
            String error = format("No default DataSource has been configured.");
            throw new IllegalArgumentException(error);
        }
        return DatabaseMetadataHelper.getMetadataAsJson(dataSource);
    }

    /**
     * Gets the product name of the database.
     *
     * @param datasourceName the datasource name
     * @return the product name
     * @throws SQLException the SQL exception
     */
    public static final String getProductName(String datasourceName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        return DatabaseMetadataHelper.getProductName(dataSource);
    }

    /**
     * Gets the product name of the database.
     *
     * @return the product name
     * @throws SQLException the SQL exception
     */
    public static final String getProductName() throws SQLException {
        DataSource dataSource = getDataSource(null);
        if (dataSource == null) {
            String error = format("No default DataSource has been configured.");
            throw new IllegalArgumentException(error);
        }
        return DatabaseMetadataHelper.getProductName(dataSource);
    }

    /**
     * Gets the data source.
     *
     * @param datasourceName the datasource name
     * @return the data source
     */
    private static DataSource getDataSource(String datasourceName) {
        return datasourceName == null ? DatabaseFacade.get()
                                                      .getDataSourcesManager()
                                                      .getDefaultDataSource()
                : DatabaseFacade.get()
                                .getDataSourcesManager()
                                .getDataSource(datasourceName);
    }

    // ============ Query ===========

    /**
     * Executes SQL query.
     *
     * @param sql the sql
     * @param parameters the parameters
     * @param datasourceName the datasource name
     * @return the result of the query as JSON
     * @throws Exception the exception
     */
    public static final String query(String sql, String parameters, String datasourceName) throws Exception {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            try {
                if (parameters != null) {
                    ParametersSetter.setParameters(parameters, preparedStatement);
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                StringWriter sw = new StringWriter();
                OutputStream output;
                try {
                    output = WriterOutputStream.builder()
                                               .setWriter(sw)
                                               .setCharset(StandardCharsets.UTF_8)
                                               .get();
                } catch (IOException e) {
                    throw new Exception(e);
                }
                DatabaseResultSetHelper.toJson(resultSet, false, false, output);
                return sw.toString();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to execute query statement [{}] in data source [{}].", sql, datasourceName, ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Executes SQL query.
     *
     * @param sql the sql
     * @param parameters the parameters
     * @return the result of the query as JSON
     * @throws Exception the exception
     */
    public static final String query(String sql, String parameters) throws Exception {
        return query(sql, parameters, null);
    }

    /**
     * Executes SQL query.
     *
     * @param sql the sql
     * @return the result of the query as JSON
     * @throws Exception the exception
     */
    public static final String query(String sql) throws Exception {
        return query(sql, null, null);
    }

    // =========== Insert ===========

    /**
     * Executes SQL insert.
     *
     * @param sql the insert statement to be executed
     * @param parameters statement parameters
     * @param datasourceName the datasource name
     * @return the generated IDs
     * @throws SQLException if an error occur
     * @throws IllegalArgumentException if the provided datasouce is not found
     * @throws RuntimeException if an error occur
     */
    public static final List<Long> insert(String sql, String parameters, String datasourceName)
            throws SQLException, IllegalArgumentException, RuntimeException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource [" + datasourceName + "] not known.");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (parameters != null) {
                ParametersSetter.setParameters(parameters, preparedStatement);
            }
            int updatedRows = preparedStatement.executeUpdate();
            List<Long> generatedIds = new ArrayList<>(updatedRows);
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                while (generatedKeys.next()) {
                    generatedIds.add(generatedKeys.getLong(1));
                }
                return generatedIds;
            }
        } catch (SQLException | RuntimeException ex) {
            logger.error("Failed to execute insert statement [{}] in data source [{}].", sql, datasourceName, ex);
            throw ex;
        }
    }

    // =========== Update ===========

    /**
     * Executes SQL update.
     *
     * @param sql the sql
     * @param parameters the parameters
     * @param datasourceName the datasource name
     * @return the number of the rows that has been changed
     * @throws Exception the exception
     */
    public static final int update(String sql, String parameters, String datasourceName) throws Exception {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            try {
                if (parameters != null) {
                    ParametersSetter.setParameters(parameters, preparedStatement);
                }
                return preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to execute update statement [{}] in data source [{}].", sql, datasourceName, ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Executes SQL update.
     *
     * @param sql the sql
     * @param parameters the parameters
     * @return the number of the rows that has been changed
     * @throws Exception the exception
     */
    public static final int update(String sql, String parameters) throws Exception {
        return update(sql, parameters, null);
    }

    /**
     * Executes SQL update.
     *
     * @param sql the sql
     * @return the number of the rows that has been changed
     * @throws Exception the exception
     */
    public static final int update(String sql) throws Exception {
        return update(sql, null, null);
    }

    /**
     * Gets the connection.
     *
     * @param datasourceName the datasource name
     * @return the connection
     * @throws SQLException the SQL exception
     */
    public static final Connection getConnection(String datasourceName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        return dataSource.getConnection();
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws SQLException the SQL exception
     */
    public static final Connection getConnection() throws SQLException {
        return getConnection(null);
    }

    // ========= Sequence ===========

    /**
     * Nextval.
     *
     * @param sequence the sequence
     * @return the long
     * @throws SQLException the SQL exception
     */
    public static long nextval(String sequence) throws SQLException {
        return nextval(sequence, null, null);
    }

    /**
     * Nextval.
     *
     * @param sequence the sequence
     * @param datasourceName the datasource name
     * @return the long
     * @throws SQLException the SQL exception
     */
    public static long nextval(String sequence, String datasourceName) throws SQLException {
        return nextval(sequence, datasourceName, null);
    }

    /**
     * Nextval.
     *
     * @param sequence the sequence
     * @param datasourceName the datasource name
     * @param tableName the table name
     * @return the nextval
     * @throws SQLException the SQL exception
     */
    public static final long nextval(String sequence, String datasourceName, String tableName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            try {
                return getNextVal(sequence, connection);
            } catch (SQLException e) {
                // assuming the sequence does not exists first time, hence create it implicitly
                if (logger.isWarnEnabled()) {
                    logger.warn(format("Implicitly creating a Sequence [{0}] due to: [{1}]", sequence, e.getMessage()));
                }
                createSequenceInternal(sequence, null, connection, tableName);
                return getNextVal(sequence, connection);
            } catch (IllegalStateException e) {
                // assuming the sequence objects are not supported by the underlying database
                PersistenceNextValueIdentityProcessor persistenceNextValueIdentityProcessor =
                        new PersistenceNextValueIdentityProcessor(null);
                return persistenceNextValueIdentityProcessor.nextval(connection, sequence);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Gets the next val.
     *
     * @param sequence the sequence
     * @param connection the connection
     * @return the next val
     * @throws SQLException the SQL exception
     */
    private static long getNextVal(String sequence, Connection connection) throws SQLException {
        String sql = SqlFactory.getNative(connection)
                               .nextval(sequence)
                               .build();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            throw new SQLException("ResultSet is empty while getting next value of the Sequence: " + sequence);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Creates the sequence internal.
     *
     * @param sequence the sequence
     * @param sequenceStart the sequence start
     * @param connection the connection
     * @param tableName the table name
     * @throws SQLException the SQL exception
     */
    private static void createSequenceInternal(String sequence, Integer sequenceStart, Connection connection, String tableName)
            throws SQLException {
        if (sequenceStart == null && tableName != null) {
            String countSql = SqlFactory.getNative(connection)
                                        .select()
                                        .column("count(*)")
                                        .from(tableName)
                                        .build();
            PreparedStatement countPreparedStatement = null;
            try {
                countPreparedStatement = connection.prepareStatement(countSql);
                ResultSet rs = countPreparedStatement.executeQuery();
                if (rs.next()) {
                    sequenceStart = rs.getInt(1);
                    sequenceStart++;
                }
            } catch (SQLException e) {
                // Do nothing
            } finally {
                if (countPreparedStatement != null) {
                    countPreparedStatement.close();
                }
            }
        }

        String sql = SqlFactory.getNative(connection)
                               .create()
                               .sequence(sequence)
                               .start(sequenceStart)
                               .build();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    /**
     * Creates the sequence.
     *
     * @param sequence the sequence
     * @param start the start
     * @param datasourceName the datasource name
     * @throws SQLException the SQL exception
     */
    public static final void createSequence(String sequence, Integer start, String datasourceName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            createSequenceInternal(sequence, start, connection, null);

        } catch (Exception ex) {
            logger.error("Failed to create sequence [{}] in data source [{}].", sequence, datasourceName, ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Creates the sequence.
     *
     * @param sequence the sequence
     * @param start the start
     * @throws SQLException the SQL exception
     */
    public static void createSequence(String sequence, Integer start) throws SQLException {
        createSequence(sequence, null, null);
    }

    /**
     * Creates the sequence.
     *
     * @param sequence the sequence
     * @throws SQLException the SQL exception
     */
    public static void createSequence(String sequence) throws SQLException {
        createSequence(sequence, null, null);
    }

    /**
     * Drop sequence.
     *
     * @param sequence the sequence
     * @param datasourceName the datasource name
     * @throws SQLException the SQL exception
     */
    public static final void dropSequence(String sequence, String datasourceName) throws SQLException {
        DataSource dataSource = getDataSource(datasourceName);
        if (dataSource == null) {
            String error = format("DataSource {0} not known.", datasourceName);
            throw new IllegalArgumentException(error);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String sql = SqlFactory.getNative(connection)
                                   .drop()
                                   .sequence(sequence)
                                   .build();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            try {
                preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }

        } catch (Exception ex) {
            logger.error("Failed to drop sequence [{}] in data source [{}].", sequence, datasourceName, ex);
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Drop sequence.
     *
     * @param sequence the sequence
     * @throws SQLException the SQL exception
     */
    public static void dropSequence(String sequence) throws SQLException {
        dropSequence(sequence, null);
    }

    // =========== SQL ===========

    /**
     * Gets the default SQL factory.
     *
     * @return the default SQL factory
     * @throws SQLException the SQL exception
     */
    public static SqlFactory getDefault() throws SQLException {
        return SqlFactory.getDefault();
    }

    /**
     * Gets a native SQL factory.
     *
     * @param connection the connection
     * @return a native SQL factory
     * @throws SQLException the SQL exception
     */
    public static SqlFactory getNative(Connection connection) throws SQLException {
        return SqlFactory.getNative(connection);
    }

}
