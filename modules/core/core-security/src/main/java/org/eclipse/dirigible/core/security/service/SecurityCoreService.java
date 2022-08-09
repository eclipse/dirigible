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
package org.eclipse.dirigible.core.security.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Security Core Service.
 */
public class SecurityCoreService implements ISecurityCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The roles persistence manager. */
	private PersistenceManager<RoleDefinition> rolesPersistenceManager = new PersistenceManager<RoleDefinition>();

	/** The access persistence manager. */
	private PersistenceManager<AccessDefinition> accessPersistenceManager = new PersistenceManager<AccessDefinition>();

	/** The Constant CACHE. */
	// used by the access security filter to minimize the performance implications on getting the whole list
	private static final List<AccessDefinition> CACHE = Collections.synchronizedList(new ArrayList<AccessDefinition>());
	
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

	// Roles

	/**
	 * Creates the role.
	 *
	 * @param name the name
	 * @param location the location
	 * @param description the description
	 * @return the role definition
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#createRole(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public RoleDefinition createRole(String name, String location, String description) throws AccessException {
		RoleDefinition roleDefinition = new RoleDefinition();
		roleDefinition.setName(name);
		roleDefinition.setLocation(location);
		roleDefinition.setDescription(description);
		roleDefinition.setCreatedBy(UserFacade.getName());
		roleDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				rolesPersistenceManager.insert(connection, roleDefinition);
				return roleDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the role.
	 *
	 * @param name the name
	 * @return the role
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getRole(java.lang.String)
	 */
	@Override
	public RoleDefinition getRole(String name) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return rolesPersistenceManager.find(connection, RoleDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Exists role.
	 *
	 * @param name the name
	 * @return true, if successful
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#existsRole(java.lang.String)
	 */
	@Override
	public boolean existsRole(String name) throws AccessException {
		return getRole(name) != null;
	}

	/**
	 * Removes the role.
	 *
	 * @param name the name
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#removeRole(java.lang.String)
	 */
	@Override
	public void removeRole(String name) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				rolesPersistenceManager.delete(connection, RoleDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Update role.
	 *
	 * @param name the name
	 * @param location the location
	 * @param description the description
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#updateRole(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void updateRole(String name, String location, String description) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				RoleDefinition roleDefinition = getRole(name);
				roleDefinition.setLocation(location);
				roleDefinition.setDescription(description);
				rolesPersistenceManager.update(connection, roleDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	/**
	 * Delete roles by location.
	 *
	 * @param location the location
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#deleteRolesByLocation(java.util.List)
	 */
	public void deleteRolesByLocation(String location) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				rolesPersistenceManager.tableCheck(connection, RoleDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SECURITY_ROLES").where("ROLE_LOCATION = ?").toString();
				PreparedStatement statement = connection.prepareStatement(sql);
				try {
					statement.setString(1, location);
					statement.executeUpdate();
				} finally {
					statement.close();
					clearCache();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	/**
	 * Delete all roles.
	 *
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#deleteAllRoles(java.util.List)
	 */
	public void deleteAllRoles() throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				rolesPersistenceManager.tableCheck(connection, RoleDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SECURITY_ROLES").toString();
				PreparedStatement statement = connection.prepareStatement(sql);
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
					clearCache();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getRoles()
	 */
	@Override
	public List<RoleDefinition> getRoles() throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return rolesPersistenceManager.findAll(connection, RoleDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Parses the roles.
	 *
	 * @param json the json
	 * @return the role definition[]
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseRoles(java.lang.String)
	 */
	@Override
	public RoleDefinition[] parseRoles(String json) {
		return GsonHelper.GSON.fromJson(json, RoleDefinition[].class);
	}

	/**
	 * Parses the roles.
	 *
	 * @param json the json
	 * @return the role definition[]
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseRoles(byte[])
	 */
	@Override
	public RoleDefinition[] parseRoles(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), RoleDefinition[].class);
	}

	/**
	 * Serialize roles.
	 *
	 * @param roles the roles
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.security.api.ISecurityCoreService#serializeRoles(org.eclipse.dirigible.core.security.
	 * definition.RoleDefinition[])
	 */
	@Override
	public String serializeRoles(RoleDefinition[] roles) {
		return GsonHelper.GSON.toJson(roles);
	}

	// Access

	/**
	 * Creates the access definition.
	 *
	 * @param location the location
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @param role the role
	 * @param description the description
	 * @param hash the hash
	 * @return the access definition
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#createAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AccessDefinition createAccessDefinition(String location, String scope, String path, String method, String role, String description, String hash)
			throws AccessException {
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setLocation(location);
		accessDefinition.setScope(scope != null ? scope : ISecurityCoreService.CONSTRAINT_SCOPE_DEFAULT);
		accessDefinition.setPath(path);
		accessDefinition.setMethod(method);
		accessDefinition.setRole(role);
		accessDefinition.setDescription(description);
		accessDefinition.setHash(hash);
		accessDefinition.setCreatedBy(UserFacade.getName());
		accessDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				accessPersistenceManager.insert(connection, accessDefinition);
				clearCache();
				return accessDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the access definition.
	 *
	 * @param id the id
	 * @return the access definition
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinition(long)
	 */
	@Override
	public AccessDefinition getAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return accessPersistenceManager.find(connection, AccessDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the access definition.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @param role the role
	 * @return the access definition
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public AccessDefinition getAccessDefinition(String scope, String path, String method, String role) throws AccessException {
		try {
			Connection connection = null;
			try {
				scope = scope != null ? scope : ISecurityCoreService.CONSTRAINT_SCOPE_DEFAULT;
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_SCOPE = ?").where("ACCESS_PATH = ?")
						.where("ACCESS_ROLE = ?").where("ACCESS_METHOD = ?").toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, scope, path, role, method);
				if (access.isEmpty()) {
					return null;
				}
				if (access.size() > 1) {
					throw new AccessException(
							format("Security Access duplication for Scope: [{0}], Path: [{1}] and Method: [{2}] with Role: [{3}]", scope, path, method, role));
				}
				return access.get(0);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}

	}

	/**
	 * Exists access definition.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @param role the role
	 * @return true, if successful
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#existsAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean existsAccessDefinition(String scope, String path, String method, String role) throws AccessException {
		return getAccessDefinition(scope, path, method, role) != null;
	}

	/**
	 * Removes the access definition.
	 *
	 * @param id the id
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#removeAccessDefinition(long)
	 */
	@Override
	public void removeAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				accessPersistenceManager.delete(connection, AccessDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Update access definition.
	 *
	 * @param id the id
	 * @param location the location
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @param role the role
	 * @param description the description
	 * @param hash the hash
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#updateAccessDefinition(long, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateAccessDefinition(long id, String location, String scope, String path, String method, String role, String description, String hash) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				AccessDefinition accessDefinition = getAccessDefinition(id);
				accessDefinition.setLocation(location);
				accessDefinition.setScope(scope != null ? scope : ISecurityCoreService.CONSTRAINT_SCOPE_DEFAULT);
				accessDefinition.setPath(path);
				accessDefinition.setMethod(method);
				accessDefinition.setRole(role);
				accessDefinition.setDescription(description);
				accessDefinition.setHash(hash);
				accessPersistenceManager.update(connection, accessDefinition);
				clearCache();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	/**
	 * Delete access definitions by location.
	 *
	 * @param location the location
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#deleteAccessDefinitionsByLocation(java.util.List)
	 */
	public void deleteAccessDefinitionsByLocation(String location) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				accessPersistenceManager.tableCheck(connection, AccessDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_LOCATION = ?").toString();
				PreparedStatement statement = connection.prepareStatement(sql);
				try {
					statement.setString(1, location);
					statement.executeUpdate();
				} finally {
					statement.close();
					clearCache();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	/**
	 * Delete all access definitions.
	 *
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#deleteAllAccessDefinitions(java.util.List)
	 */
	public void deleteAllAccessDefinitions() throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				accessPersistenceManager.tableCheck(connection, AccessDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SECURITY_ACCESS").toString();
				PreparedStatement statement = connection.prepareStatement(sql);
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
					clearCache();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the access definitions.
	 *
	 * @return the access definitions
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinitions()
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitions() throws AccessException {
		if (!CACHE.isEmpty()) {
			return Collections.unmodifiableList(CACHE);
		}
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				List<AccessDefinition> accessDefinitions = accessPersistenceManager.findAll(connection, AccessDefinition.class);
				CACHE.addAll(accessDefinitions);
				return Collections.unmodifiableList(accessDefinitions);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the access definitions by path.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @return the access definitions by path
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinitionsByUri(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByPath(String scope, String path) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_SCOPE = ?").where("ACCESS_PATH = ?")
						.toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, scope, path);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Gets the access definitions by path and method.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @return the access definitions by path and method
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinitionsByUriAndMethod(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByPathAndMethod(String scope, String path, String method) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_SCOPE = ?").where("ACCESS_PATH = ?")
						.where(SqlFactory.getNative(connection).expression().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString()).toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, scope, path, method, AccessDefinition.METHOD_ANY);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Checks if is access allowed.
	 *
	 * @param scope the scope
	 * @param path the path
	 * @param method the method
	 * @param role the role
	 * @return true, if is access allowed
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#isAccessAllowed(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isAccessAllowed(String scope, String path, String method, String role) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_SCOPE = ?").where("ACCESS_PATH = ?")
						.where("ACCESS_ROLE = ?")
						.where(SqlFactory.getNative(connection).expression().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString()).toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, scope, path, role, method,
						AccessDefinition.METHOD_ANY);
				return !access.isEmpty();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/**
	 * Parses the access definitions.
	 *
	 * @param json the json
	 * @return the list
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseAccessDefinitions(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> parseAccessDefinitions(String json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}

	/**
	 * Parses the access definitions.
	 *
	 * @param json the json
	 * @return the list
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseAccessDefinitions(byte[])
	 */
	@Override
	public List<AccessDefinition> parseAccessDefinitions(byte[] json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}

	/**
	 * Serialize access definitions.
	 *
	 * @param accessDefinitions the access definitions
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#serializeAccessDefinitions(java.util.List)
	 */
	@Override
	public String serializeAccessDefinitions(List<AccessDefinition> accessDefinitions) {
		AccessArtifact accessArtifact = AccessArtifact.combine(accessDefinitions);
		return accessArtifact.serialize();
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		CACHE.clear();
	}

	/**
	 * Drop modified access definitions.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @throws AccessException the access exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#dropModifiedAccessDefinitions(java.util.List)
	 */
	public void dropModifiedAccessDefinitions(String location, String hash) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_LOCATION = ? AND ACCESS_HASH <> ?").toString();
				PreparedStatement statement = connection.prepareStatement(sql);
				try {
					statement.setString(1, location);
					statement.setString(2, hash);
					statement.executeUpdate();
				} finally {
					statement.close();
					clearCache();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

}
