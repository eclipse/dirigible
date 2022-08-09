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
package org.eclipse.dirigible.engine.odata2.service;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.*;
import org.eclipse.dirigible.engine.odata2.definition.factory.ODataDefinitionFactory;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * The Class ODataCoreService.
 */
public class ODataCoreService implements IODataCoreService {

    /** The data source. */
    private DataSource dataSource = null;

    /** The odata schema persistence manager. */
    private PersistenceManager<ODataSchemaDefinition> odataSchemaPersistenceManager = new PersistenceManager<ODataSchemaDefinition>();

    /** The odata mapping persistence manager. */
    private PersistenceManager<ODataMappingDefinition> odataMappingPersistenceManager = new PersistenceManager<ODataMappingDefinition>();

    /** The odata container persistence manager. */
    private PersistenceManager<ODataContainerDefinition> odataContainerPersistenceManager = new PersistenceManager<ODataContainerDefinition>();

    /** The odata persistence manager. */
    private PersistenceManager<ODataDefinition> odataPersistenceManager = new PersistenceManager<ODataDefinition>();

    /** The odata handler persistence manager. */
    private PersistenceManager<ODataHandlerDefinition> odataHandlerPersistenceManager = new PersistenceManager<ODataHandlerDefinition>();

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
     * Creates the schema.
     *
     * @param location the location
     * @param content the content
     * @return the o data schema definition
     * @throws ODataException the o data exception
     */
    @Override
    public ODataSchemaDefinition createSchema(String location, byte[] content) throws ODataException {
        ODataSchemaDefinition odataSchemaDefinition = new ODataSchemaDefinition();
        odataSchemaDefinition.setLocation(location);
        odataSchemaDefinition.setContent(content);
        odataSchemaDefinition.setCreatedBy(UserFacade.getName());
        odataSchemaDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataSchemaPersistenceManager.insert(connection, odataSchemaDefinition);
                return odataSchemaDefinition;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the schema.
     *
     * @param location the location
     * @return the schema
     * @throws ODataException the o data exception
     */
    @Override
    public ODataSchemaDefinition getSchema(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataSchemaPersistenceManager.find(connection, ODataSchemaDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Exists schema.
     *
     * @param location the location
     * @return true, if successful
     * @throws ODataException the o data exception
     */
    @Override
    public boolean existsSchema(String location) throws ODataException {
        return getSchema(location) != null;
    }

    /**
     * Removes the schema.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    @Override
    public void removeSchema(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataSchemaPersistenceManager.delete(connection, ODataSchemaDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Update schema.
     *
     * @param location the location
     * @param content the content
     * @throws ODataException the o data exception
     */
    @Override
    public void updateSchema(String location, byte[] content) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                ODataSchemaDefinition odataSchemaDefinition = getSchema(location);
                odataSchemaDefinition.setContent(content);
                odataSchemaPersistenceManager.update(connection, odataSchemaDefinition);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the schemas.
     *
     * @return the schemas
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataSchemaDefinition> getSchemas() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataSchemaPersistenceManager.findAll(connection, ODataSchemaDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Creates the mapping.
     *
     * @param location the location
     * @param content the content
     * @return the o data mapping definition
     * @throws ODataException the o data exception
     */
    @Override
    public ODataMappingDefinition createMapping(String location, byte[] content) throws ODataException {
        ODataMappingDefinition odataMappingDefinition = new ODataMappingDefinition();
        odataMappingDefinition.setLocation(location);
        odataMappingDefinition.setContent(content);
        odataMappingDefinition.setCreatedBy(UserFacade.getName());
        odataMappingDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataMappingPersistenceManager.insert(connection, odataMappingDefinition);
                return odataMappingDefinition;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the mapping.
     *
     * @param location the location
     * @return the mapping
     * @throws ODataException the o data exception
     */
    @Override
    public ODataMappingDefinition getMapping(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataMappingPersistenceManager.find(connection, ODataMappingDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Exists mapping.
     *
     * @param location the location
     * @return true, if successful
     * @throws ODataException the o data exception
     */
    @Override
    public boolean existsMapping(String location) throws ODataException {
        return getMapping(location) != null;
    }

    /**
     * Removes the mapping.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    @Override
    public void removeMapping(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataMappingPersistenceManager.delete(connection, ODataMappingDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Removes the mappings.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    @Override
    public void removeMappings(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
            	odataMappingPersistenceManager.tableCheck(connection, ODataMappingDefinition.class);
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_MAPPING").where("ODATAM_LOCATION LIKE ?").toString();
                odataMappingPersistenceManager.execute(connection, sql, location + "#%");
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Update mapping.
     *
     * @param location the location
     * @param content the content
     * @throws ODataException the o data exception
     */
    @Override
    public void updateMapping(String location, byte[] content) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                ODataMappingDefinition odataMappingDefinition = getMapping(location);
                odataMappingDefinition.setContent(content);
                odataMappingPersistenceManager.update(connection, odataMappingDefinition);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the mappings.
     *
     * @return the mappings
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataMappingDefinition> getMappings() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataMappingPersistenceManager.findAll(connection, ODataMappingDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


    /**
     * Creates the container.
     *
     * @param location the location
     * @param content the content
     * @return the o data container definition
     * @throws ODataException the o data exception
     */
    @Override
    public ODataContainerDefinition createContainer(String location, byte[] content) throws ODataException {
        ODataContainerDefinition odataContainerDefinition = new ODataContainerDefinition();
        odataContainerDefinition.setLocation(location);
        odataContainerDefinition.setContent(content);
        odataContainerDefinition.setCreatedBy(UserFacade.getName());
        odataContainerDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataContainerPersistenceManager.insert(connection, odataContainerDefinition);
                return odataContainerDefinition;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the container.
     *
     * @param location the location
     * @return the container
     * @throws ODataException the o data exception
     */
    @Override
    public ODataContainerDefinition getContainer(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataContainerPersistenceManager.find(connection, ODataContainerDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Exists container.
     *
     * @param location the location
     * @return true, if successful
     * @throws ODataException the o data exception
     */
    @Override
    public boolean existsContainer(String location) throws ODataException {
        return getContainer(location) != null;
    }

    /**
     * Removes the container.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    @Override
    public void removeContainer(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataContainerPersistenceManager.delete(connection, ODataContainerDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Update container.
     *
     * @param location the location
     * @param content the content
     * @throws ODataException the o data exception
     */
    @Override
    public void updateContainer(String location, byte[] content) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                ODataContainerDefinition odataContainerDefinition = getContainer(location);
                odataContainerDefinition.setContent(content);
                odataContainerPersistenceManager.update(connection, odataContainerDefinition);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the containers.
     *
     * @return the containers
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataContainerDefinition> getContainers() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataContainerPersistenceManager.findAll(connection, ODataContainerDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


    /**
     * Gets the metadata.
     *
     * @return the metadata
     * @throws ODataException the o data exception
     */
    @Override
    public InputStream getMetadata() throws ODataException {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
        builder.append("<edmx:Edmx xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\"  xmlns:sap=\"http://www.sap.com/Protocols/SAPData\" Version=\"1.0\">\n");
        builder.append("    <edmx:DataServices m:DataServiceVersion=\"1.0\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">\n");

        List<ODataSchemaDefinition> schemas = getSchemas();
        for (ODataSchemaDefinition schema : schemas) {
            builder.append(new String(schema.getContent()));
            builder.append("\n");
        }

        builder.append("<Schema Namespace=\"").append("Default").append("\"\n")
                .append("    xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n");
        builder.append("    <EntityContainer Name=\"").append("Default").append("EntityContainer\" m:IsDefaultEntityContainer=\"true\">\n");
        List<ODataContainerDefinition> containers = getContainers();
        for (ODataContainerDefinition container : containers) {
            builder.append(new String(container.getContent()));
            builder.append("\n");
        }
        builder.append("    </EntityContainer>\n");
        builder.append("</Schema>\n");

        builder.append("    </edmx:DataServices>\n");
        builder.append("</edmx:Edmx>\n");

        return new ByteArrayInputStream(builder.toString().getBytes());
    }

    /**
     * Parses the O data.
     *
     * @param contentPath the content path
     * @param data the data
     * @return the o data definition
     */
    @Override
    public ODataDefinition parseOData(String contentPath, String data) {
        return ODataDefinitionFactory.parseOData(contentPath, data);
    }

    /**
     * Exists O data.
     *
     * @param location the location
     * @return true, if successful
     * @throws ODataException the o data exception
     */
    @Override
    public boolean existsOData(String location) throws ODataException {
        return getOData(location) != null;
    }

    /**
     * Creates the O data.
     *
     * @param location the location
     * @param namespace the namespace
     * @param hash the hash
     * @return the o data definition
     * @throws ODataException the o data exception
     */
    @Override
    public ODataDefinition createOData(String location, String namespace, String hash) throws ODataException {
        ODataDefinition odataModel = new ODataDefinition();
        odataModel.setLocation(location);
        odataModel.setNamespace(namespace);
        odataModel.setHash(hash);
        odataModel.setCreatedBy(UserFacade.getName());
        odataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataPersistenceManager.insert(connection, odataModel);
                return odataModel;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }

    }

    /**
     * Gets the o data.
     *
     * @param location the location
     * @return the o data
     * @throws ODataException the o data exception
     */
    @Override
    public ODataDefinition getOData(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataPersistenceManager.find(connection, ODataDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Update O data.
     *
     * @param location the location
     * @param namespace the namespace
     * @param hash the hash
     * @throws ODataException the o data exception
     */
    @Override
    public void updateOData(String location, String namespace, String hash) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                ODataDefinition odataModel = getOData(location);
                odataModel.setNamespace(namespace);
                odataModel.setHash(hash);
                odataPersistenceManager.update(connection, odataModel);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the o datas.
     *
     * @return the o datas
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataDefinition> getODatas() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataPersistenceManager.findAll(connection, ODataDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Removes the O data.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    public void removeOData(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataPersistenceManager.delete(connection, ODataDefinition.class, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Creates the handler.
     *
     * @param location the location
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @param handler the handler
     * @return the o data handler definition
     * @throws ODataException the o data exception
     */
    @Override
    public ODataHandlerDefinition createHandler(String location, String namespace, String name, String method,
                                                String type, String handler) throws ODataException {
        ODataHandlerDefinition odataHandlerDefinition = new ODataHandlerDefinition();
        odataHandlerDefinition.setLocation(location);
        odataHandlerDefinition.setNamespace(namespace);
        odataHandlerDefinition.setName(name);
        odataHandlerDefinition.setMethod(method);
        odataHandlerDefinition.setType(type);
        if (handler == null) handler = "N/A";
        odataHandlerDefinition.setHandler(handler);
        odataHandlerDefinition.setCreatedBy(UserFacade.getName());
        odataHandlerDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataHandlerPersistenceManager.insert(connection, odataHandlerDefinition);
                return odataHandlerDefinition;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the handler.
     *
     * @param location the location
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @return the handler
     * @throws ODataException the o data exception
     */
    @Override
    public ODataHandlerDefinition getHandler(String location, String namespace, String name, String method, String type)
            throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_LOCATION = ?")
                        .where("ODATAH_NAMESPACE = ?")
                        .where("ODATAH_NAME = ?")
                        .where("ODATAH_METHOD = ?")
                        .where("ODATAH_TYPE = ?")
                        .build();
                List<ODataHandlerDefinition> handlerModels = odataHandlerPersistenceManager.query(connection, ODataHandlerDefinition.class, sql,
                        location, namespace, name, method, type);
                return handlerModels.size() > 0 ? handlerModels.get(0) : null;
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Gets the handlers.
     *
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @return the handlers
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataHandlerDefinition> getHandlers(String namespace, String name, String method, String type)
            throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_NAMESPACE = ?")
                        .where("ODATAH_NAME = ?")
                        .where("ODATAH_METHOD = ?")
                        .where("ODATAH_TYPE = ?")
                        .build();
                return odataHandlerPersistenceManager.query(connection, ODataHandlerDefinition.class, sql,
                        namespace, name, method, type);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Exists handler.
     *
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @return true, if successful
     * @throws ODataException the o data exception
     */
    @Override
    public boolean existsHandler(String namespace, String name, String method, String type) throws ODataException {
        return getHandlers(namespace, name, method, type).size() > 0;
    }

    /**
     * Removes the handler.
     *
     * @param location the location
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @throws ODataException the o data exception
     */
    @Override
    public void removeHandler(String location, String namespace, String name, String method, String type)
            throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_LOCATION = ?")
                        .where("ODATAH_NAMESPACE = ?")
                        .where("ODATAH_NAME = ?")
                        .where("ODATAH_METHOD = ?")
                        .where("ODATAH_TYPE = ?")
                        .build();
                odataHandlerPersistenceManager.execute(connection, sql, location, namespace, name, method, type);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Removes the handlers.
     *
     * @param location the location
     * @throws ODataException the o data exception
     */
    @Override
    public void removeHandlers(String location) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_LOCATION = ?")
                        .build();
                odataHandlerPersistenceManager.execute(connection, sql, location);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    /**
     * Update handler.
     *
     * @param location the location
     * @param namespace the namespace
     * @param name the name
     * @param method the method
     * @param type the type
     * @param handler the handler
     * @throws ODataException the o data exception
     */
    @Override
    public void updateHandler(String location, String namespace, String name, String method, String type,
                              String handler) throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                ODataHandlerDefinition odataHandlerDefinition = getHandler(location, namespace, name, method, type);
                odataHandlerDefinition.setLocation(location);
                odataHandlerDefinition.setNamespace(namespace);
                odataHandlerDefinition.setName(name);
                odataHandlerDefinition.setMethod(method);
                odataHandlerDefinition.setType(type);
                odataHandlerDefinition.setHandler(handler);
                odataHandlerPersistenceManager.update(connection, odataHandlerDefinition);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }

    }

    /**
     * Gets the all handlers.
     *
     * @return the all handlers
     * @throws ODataException the o data exception
     */
    @Override
    public List<ODataHandlerDefinition> getAllHandlers() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                return odataHandlerPersistenceManager.findAll(connection, ODataHandlerDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


    /**
     * Handler definition table check.
     *
     * @throws ODataException the o data exception
     */
    public void handlerDefinitionTableCheck() throws ODataException {
        try {
            try (Connection connection = getDataSource().getConnection()) {
                odataHandlerPersistenceManager.tableCheck(connection, ODataHandlerDefinition.class);
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


}
