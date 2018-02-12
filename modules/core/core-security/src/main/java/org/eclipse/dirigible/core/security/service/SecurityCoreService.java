/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.security.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
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
@Singleton
public class SecurityCoreService implements ISecurityCoreService {

	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<RoleDefinition> rolesPersistenceManager;

	@Inject
	private PersistenceManager<AccessDefinition> accessPersistenceManager;

	// used by the access security filter to minimize the performance implications on getting the whole list
	private static final List<AccessDefinition> CACHE = Collections.synchronizedList(new ArrayList<AccessDefinition>());

	// Roles

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
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getRole(java.lang.String)
	 */
	@Override
	public RoleDefinition getRole(String name) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#existsRole(java.lang.String)
	 */
	@Override
	public boolean existsRole(String name) throws AccessException {
		return getRole(name) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#removeRole(java.lang.String)
	 */
	@Override
	public void removeRole(String name) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getRoles()
	 */
	@Override
	public List<RoleDefinition> getRoles() throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseRoles(java.lang.String)
	 */
	@Override
	public RoleDefinition[] parseRoles(String json) {
		return GsonHelper.GSON.fromJson(json, RoleDefinition[].class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseRoles(byte[])
	 */
	@Override
	public RoleDefinition[] parseRoles(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), RoleDefinition[].class);
	}

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#createAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AccessDefinition createAccessDefinition(String location, String uri, String method, String role, String description)
			throws AccessException {
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setLocation(location);
		accessDefinition.setUri(uri);
		accessDefinition.setMethod(method);
		accessDefinition.setRole(role);
		accessDefinition.setDescription(description);
		accessDefinition.setCreatedBy(UserFacade.getName());
		accessDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinition(long)
	 */
	@Override
	public AccessDefinition getAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public AccessDefinition getAccessDefinition(String uri, String method, String role) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_URI = ?")
						.where("ACCESS_ROLE = ?").where("ACCESS_METHOD = ?").toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, uri, role, method);
				if (access.isEmpty()) {
					return null;
				}
				if (access.size() > 1) {
					throw new AccessException(
							format("Security Access duplication for URI [{0}] and Method [{1}] with Role [{2}]", uri, method, role));
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#existsAccessDefinition(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean existsAccessDefinition(String uri, String method, String role) throws AccessException {
		return getAccessDefinition(uri, method, role) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#removeAccessDefinition(long)
	 */
	@Override
	public void removeAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#updateAccessDefinition(long, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateAccessDefinition(long id, String location, String uri, String method, String role, String description) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				AccessDefinition accessDefinition = getAccessDefinition(id);
				accessDefinition.setLocation(location);
				accessDefinition.setUri(uri);
				accessDefinition.setMethod(method);
				accessDefinition.setRole(role);
				accessDefinition.setDescription(description);
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
				connection = dataSource.getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinitionsByUri(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByUri(String uri) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_URI = ?")
						.toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, Arrays.asList(uri));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.security.api.ISecurityCoreService#getAccessDefinitionsByUriAndMethod(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByUriAndMethod(String uri, String method) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_URI = ?")
						.where(SqlFactory.getNative(connection).expression().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString()).toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, uri, method, AccessDefinition.METHOD_ANY);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#isAccessAllowed(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isAccessAllowed(String uri, String method, String role) throws AccessException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_SECURITY_ACCESS").where("ACCESS_URI = ?")
						.where("ACCESS_ROLE = ?")
						.where(SqlFactory.getNative(connection).expression().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString()).toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, uri, role, method,
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseAccessDefinitions(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> parseAccessDefinitions(String json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.api.ISecurityCoreService#parseAccessDefinitions(byte[])
	 */
	@Override
	public List<AccessDefinition> parseAccessDefinitions(byte[] json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}

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

}
