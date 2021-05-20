/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.service;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.*;
import org.eclipse.dirigible.engine.odata2.definition.factory.ODataDefinitionFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ODataCoreService implements IODataCoreService {

    @Inject
    private DataSource dataSource;

    @Inject
    private PersistenceManager<ODataSchemaDefinition> odataSchemaPersistenceManager;

    @Inject
    private PersistenceManager<ODataMappingDefinition> odataMappingPersistenceManager;

    @Inject
    private PersistenceManager<ODataContainerDefinition> odataContainerPersistenceManager;

    @Inject
    private PersistenceManager<ODataDefinition> odataPersistenceManager;

    @Inject
    private PersistenceManager<ODataHandlerDefinition> odataHandlerPersistenceManager;


    @Override
    public ODataSchemaDefinition createSchema(String location, byte[] content) throws ODataException {
        ODataSchemaDefinition odataSchemaDefinition = new ODataSchemaDefinition();
        odataSchemaDefinition.setLocation(location);
        odataSchemaDefinition.setContent(content);
        odataSchemaDefinition.setCreatedBy(UserFacade.getName());
        odataSchemaDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataSchemaPersistenceManager.insert(connection, odataSchemaDefinition);
                return odataSchemaDefinition;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataSchemaDefinition getSchema(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataSchemaPersistenceManager.find(connection, ODataSchemaDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public boolean existsSchema(String location) throws ODataException {
        return getSchema(location) != null;
    }

    @Override
    public void removeSchema(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataSchemaPersistenceManager.delete(connection, ODataSchemaDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void updateSchema(String location, byte[] content) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                ODataSchemaDefinition odataSchemaDefinition = getSchema(location);
                odataSchemaDefinition.setContent(content);
                odataSchemaPersistenceManager.update(connection, odataSchemaDefinition);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public List<ODataSchemaDefinition> getSchemas() throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataSchemaPersistenceManager.findAll(connection, ODataSchemaDefinition.class);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataMappingDefinition createMapping(String location, byte[] content) throws ODataException {
        ODataMappingDefinition odataMappingDefinition = new ODataMappingDefinition();
        odataMappingDefinition.setLocation(location);
        odataMappingDefinition.setContent(content);
        odataMappingDefinition.setCreatedBy(UserFacade.getName());
        odataMappingDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataMappingPersistenceManager.insert(connection, odataMappingDefinition);
                return odataMappingDefinition;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataMappingDefinition getMapping(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataMappingPersistenceManager.find(connection, ODataMappingDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public boolean existsMapping(String location) throws ODataException {
        return getMapping(location) != null;
    }

    @Override
    public void removeMapping(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataMappingPersistenceManager.delete(connection, ODataMappingDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void removeMappings(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_MAPPING").where("ODATAM_LOCATION LIKE ?").toString();
                odataMappingPersistenceManager.execute(connection, sql, location + "#%");
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void updateMapping(String location, byte[] content) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                ODataMappingDefinition odataMappingDefinition = getMapping(location);
                odataMappingDefinition.setContent(content);
                odataMappingPersistenceManager.update(connection, odataMappingDefinition);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public List<ODataMappingDefinition> getMappings() throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataMappingPersistenceManager.findAll(connection, ODataMappingDefinition.class);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


    @Override
    public ODataContainerDefinition createContainer(String location, byte[] content) throws ODataException {
        ODataContainerDefinition odataContainerDefinition = new ODataContainerDefinition();
        odataContainerDefinition.setLocation(location);
        odataContainerDefinition.setContent(content);
        odataContainerDefinition.setCreatedBy(UserFacade.getName());
        odataContainerDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataContainerPersistenceManager.insert(connection, odataContainerDefinition);
                return odataContainerDefinition;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataContainerDefinition getContainer(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataContainerPersistenceManager.find(connection, ODataContainerDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public boolean existsContainer(String location) throws ODataException {
        return getContainer(location) != null;
    }

    @Override
    public void removeContainer(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataContainerPersistenceManager.delete(connection, ODataContainerDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void updateContainer(String location, byte[] content) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                ODataContainerDefinition odataContainerDefinition = getContainer(location);
                odataContainerDefinition.setContent(content);
                odataContainerPersistenceManager.update(connection, odataContainerDefinition);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public List<ODataContainerDefinition> getContainers() throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataContainerPersistenceManager.findAll(connection, ODataContainerDefinition.class);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


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

    @Override
    public ODataDefinition parseOData(String contentPath, String data) {
        return ODataDefinitionFactory.parseOData(contentPath, data);
    }

    @Override
    public boolean existsOData(String location) throws ODataException {
        return getOData(location) != null;
    }

    @Override
    public ODataDefinition createOData(String location, String namespace, String hash) throws ODataException {
        ODataDefinition odataModel = new ODataDefinition();
        odataModel.setLocation(location);
        odataModel.setNamespace(namespace);
        odataModel.setHash(hash);
        odataModel.setCreatedBy(UserFacade.getName());
        odataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataPersistenceManager.insert(connection, odataModel);
                return odataModel;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }

    }

    @Override
    public ODataDefinition getOData(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataPersistenceManager.find(connection, ODataDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void updateOData(String location, String namespace, String hash) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                ODataDefinition odataModel = getOData(location);
                odataModel.setNamespace(namespace);
                odataModel.setHash(hash);
                odataPersistenceManager.update(connection, odataModel);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public List<ODataDefinition> getODatas() throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataPersistenceManager.findAll(connection, ODataDefinition.class);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    public void removeOData(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataPersistenceManager.delete(connection, ODataDefinition.class, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

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
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                odataHandlerPersistenceManager.insert(connection, odataHandlerDefinition);
                return odataHandlerDefinition;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataHandlerDefinition getHandler(String location, String namespace, String name, String method, String type)
            throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
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
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public List<ODataHandlerDefinition> getHandlers(String namespace, String name, String method, String type)
            throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_NAMESPACE = ?")
                        .where("ODATAH_NAME = ?")
                        .where("ODATAH_METHOD = ?")
                        .where("ODATAH_TYPE = ?")
                        .build();
                List<ODataHandlerDefinition> handlerModels = odataHandlerPersistenceManager.query(connection, ODataHandlerDefinition.class, sql,
                        namespace, name, method, type);
                return handlerModels;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public boolean existsHandler(String namespace, String name, String method, String type) throws ODataException {
        return getHandlers(namespace, name, method, type).size() > 0;
    }

    @Override
    public void removeHandler(String location, String namespace, String name, String method, String type)
            throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_LOCATION = ?")
                        .where("ODATAH_NAMESPACE = ?")
                        .where("ODATAH_NAME = ?")
                        .where("ODATAH_METHOD = ?")
                        .where("ODATAH_TYPE = ?")
                        .build();
                odataHandlerPersistenceManager.execute(connection, sql, location, namespace, name, method, type);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void removeHandlers(String location) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_ODATA_HANDLER")
                        .where("ODATAH_LOCATION = ?")
                        .build();
                odataHandlerPersistenceManager.execute(connection, sql, location);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public void updateHandler(String location, String namespace, String name, String method, String type,
                              String handler) throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                ODataHandlerDefinition odataHandlerDefinition = getHandler(location, namespace, name, method, type);
                odataHandlerDefinition.setLocation(location);
                odataHandlerDefinition.setNamespace(namespace);
                odataHandlerDefinition.setName(name);
                odataHandlerDefinition.setMethod(method);
                odataHandlerDefinition.setType(type);
                odataHandlerDefinition.setHandler(handler);
                odataHandlerPersistenceManager.update(connection, odataHandlerDefinition);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }

    }

    @Override
    public List<ODataHandlerDefinition> getAllHandlers() throws ODataException {
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                return odataHandlerPersistenceManager.findAll(connection, ODataHandlerDefinition.class);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }


}
