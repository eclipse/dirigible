/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;

public class ODataCoreService implements IODataCoreService {
	
	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<ODataSchemaDefinition> odataSchemaPersistenceManager;
	
	@Inject
	private PersistenceManager<ODataMappingDefinition> odataMappingPersistenceManager;
	
	@Inject
	private PersistenceManager<ODataDefinition> odataPersistenceManager;


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
	public InputStream getMetadata() throws ODataException {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		builder.append("<edmx:Edmx xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" Version=\"1.0\">\n");
		builder.append("    <edmx:DataServices m:DataServiceVersion=\"1.0\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">\n");
		
		List<ODataSchemaDefinition> schemas = getSchemas();
		for (ODataSchemaDefinition schema : schemas) {
			builder.append(new String(schema.getContent()));
			builder.append("\n");
		}
		
		builder.append("    </edmx:DataServices>\n");
		builder.append("</edmx:Edmx>\n");
		
		return new ByteArrayInputStream(builder.toString().getBytes());
	}

	@Override
	public ODataDefinition parseOData(String contentPath, String data) {
		ODataDefinition odataDefinition = GsonHelper.GSON.fromJson(data, ODataDefinition.class);
		odataDefinition.setLocation(contentPath);
		odataDefinition.setHash(DigestUtils.md5Hex(data));
		odataDefinition.setCreatedBy(UserFacade.getName());
		odataDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		return odataDefinition;
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
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_ODATA").toString();
				List<ODataDefinition> tableModels = odataPersistenceManager.query(connection, ODataDefinition.class, sql);
				return tableModels;
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

}
