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
package org.eclipse.dirigible.core.extensions.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Class ExtensionsCoreService.
 */
public class ExtensionsCoreService implements IExtensionsCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The extension point persistence manager. */
	private PersistenceManager<ExtensionPointDefinition> extensionPointPersistenceManager = new PersistenceManager<ExtensionPointDefinition>();

	/** The extension persistence manager. */
	private PersistenceManager<ExtensionDefinition> extensionPersistenceManager = new PersistenceManager<ExtensionDefinition>();
	
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

	// Extension Points

	/**
	 * Creates the extension point.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @return the extension point definition
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#createExtensionPoint(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ExtensionPointDefinition createExtensionPoint(String location, String name, String description) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = new ExtensionPointDefinition();
		extensionPointDefinition.setLocation(location);
		extensionPointDefinition.setName(name);
		extensionPointDefinition.setDescription(description);
		extensionPointDefinition.setCreatedBy(UserFacade.getName());
		extensionPointDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				extensionPointPersistenceManager.insert(connection, extensionPointDefinition);
				return extensionPointDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extension point.
	 *
	 * @param location the location
	 * @return the extension point
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtensionPoint(java.lang.String)
	 */
	@Override
	public ExtensionPointDefinition getExtensionPoint(String location) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return extensionPointPersistenceManager.find(connection, ExtensionPointDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extension point by name.
	 *
	 * @param name the name
	 * @return the extension point by name
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtensionPointByName(java.lang.String)
	 */
	@Override
	public ExtensionPointDefinition getExtensionPointByName(String name) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_EXTENSION_POINTS").where("EXTENSIONPOINT_NAME = ?")
						.toString();
				List<ExtensionPointDefinition> extensionPointDefinitions = extensionPointPersistenceManager.query(connection,
						ExtensionPointDefinition.class, sql, Arrays.asList(name));
				if (extensionPointDefinitions.isEmpty()) {
					return null;
				}
				if (extensionPointDefinitions.size() > 1) {
					throw new ExtensionsException(
							format("There are more that one ExtensionPoints with the same name [{0}] at locations: [{1}] and [{2}].", name,
									extensionPointDefinitions.get(0).getLocation(), extensionPointDefinitions.get(1).getLocation()));
				}
				return extensionPointDefinitions.get(0);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Removes the extension point.
	 *
	 * @param location the location
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#removeExtensionPoint(java.lang.String)
	 */
	@Override
	public void removeExtensionPoint(String location) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				extensionPointPersistenceManager.delete(connection, ExtensionPointDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Update extension point.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#updateExtensionPoint(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateExtensionPoint(String location, String name, String description) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				ExtensionPointDefinition extensionPointDefinition = getExtensionPoint(location);
				extensionPointDefinition.setName(name);
				extensionPointDefinition.setDescription(description);
				extensionPointPersistenceManager.update(connection, extensionPointDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extension points.
	 *
	 * @return the extension points
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtensionPoints()
	 */
	@Override
	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return extensionPointPersistenceManager.findAll(connection, ExtensionPointDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	// Extensions

	/**
	 * Creates the extension.
	 *
	 * @param location the location
	 * @param module the module
	 * @param extensionPoint the extension point
	 * @param description the description
	 * @return the extension definition
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#createExtension(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ExtensionDefinition createExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException {
		ExtensionDefinition extensionDefinition = new ExtensionDefinition();
		extensionDefinition.setLocation(location);
		extensionDefinition.setModule(module);
		extensionDefinition.setExtensionPoint(extensionPoint);
		extensionDefinition.setDescription(description);
		extensionDefinition.setCreatedBy(UserFacade.getName());
		extensionDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				extensionPersistenceManager.insert(connection, extensionDefinition);
				return extensionDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extension.
	 *
	 * @param location the location
	 * @return the extension
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtension(java.lang.String)
	 */
	@Override
	public ExtensionDefinition getExtension(String location) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return extensionPersistenceManager.find(connection, ExtensionDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Removes the extension.
	 *
	 * @param location the location
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#removeExtension(java.lang.String)
	 */
	@Override
	public void removeExtension(String location) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				extensionPersistenceManager.delete(connection, ExtensionDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Update extension.
	 *
	 * @param location the location
	 * @param module the module
	 * @param extensionPoint the extension point
	 * @param description the description
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#updateExtension(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				ExtensionDefinition extensionDefinition = getExtension(location);
				extensionDefinition.setModule(module);
				extensionDefinition.setExtensionPoint(extensionPoint);
				extensionDefinition.setDescription(description);
				extensionPersistenceManager.update(connection, extensionDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extensions.
	 *
	 * @return the extensions
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtensions()
	 */
	@Override
	public List<ExtensionDefinition> getExtensions() throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return extensionPersistenceManager.findAll(connection, ExtensionDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Gets the extensions by extension point.
	 *
	 * @param extensionPoint the extension point
	 * @return the extensions by extension point
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#getExtensionsByExtensionPoint(java.lang.String)
	 */
	@Override
	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_EXTENSIONS")
						.where("EXTENSION_EXTENSIONPOINT_NAME = ?").toString();
				return extensionPersistenceManager.query(connection, ExtensionDefinition.class, sql,
						Arrays.asList(extensionPoint));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	/**
	 * Exists extension point.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#existsExtensionPoint(java.lang.String)
	 */
	@Override
	public boolean existsExtensionPoint(String location) throws ExtensionsException {
		return getExtensionPoint(location) != null;
	}

	/**
	 * Exists extension.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws ExtensionsException the extensions exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#existsExtension(java.lang.String)
	 */
	@Override
	public boolean existsExtension(String location) throws ExtensionsException {
		return getExtension(location) != null;
	}

	/**
	 * Parses the extension point.
	 *
	 * @param json the json
	 * @return the extension point definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#parseExtensionPoint(java.lang.String)
	 */
	@Override
	public ExtensionPointDefinition parseExtensionPoint(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionPointDefinition.class);
	}

	/**
	 * Parses the extension.
	 *
	 * @param json the json
	 * @return the extension definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#parseExtension(java.lang.String)
	 */
	@Override
	public ExtensionDefinition parseExtension(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionDefinition.class);
	}

	/**
	 * Parses the extension point.
	 *
	 * @param json the json
	 * @return the extension point definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#parseExtensionPoint(byte[])
	 */
	@Override
	public ExtensionPointDefinition parseExtensionPoint(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8),
				ExtensionPointDefinition.class);
	}

	/**
	 * Parses the extension.
	 *
	 * @param json the json
	 * @return the extension definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#parseExtension(byte[])
	 */
	@Override
	public ExtensionDefinition parseExtension(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), ExtensionDefinition.class);
	}

	/**
	 * Serialize extension point.
	 *
	 * @param extensionPointDefinition the extension point definition
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#serializeExtensionPoint(org.eclipse.dirigible.
	 * core.extensions.definition.ExtensionPointDefinition)
	 */
	@Override
	public String serializeExtensionPoint(ExtensionPointDefinition extensionPointDefinition) {
		return GsonHelper.GSON.toJson(extensionPointDefinition);
	}

	/**
	 * Serialize extension.
	 *
	 * @param extensionDefinition the extension definition
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService#serializeExtension(org.eclipse.dirigible.core.
	 * extensions.definition.ExtensionDefinition)
	 */
	@Override
	public String serializeExtension(ExtensionDefinition extensionDefinition) {
		return GsonHelper.GSON.toJson(extensionDefinition);
	}

}
