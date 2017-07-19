package org.eclipse.dirigible.core.security.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessArtifact;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.squle.Squle;

@Singleton
public class SecurityCoreService implements ISecurityCoreService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private PersistenceManager<RoleDefinition> rolesPersistenceManager;
	
	@Inject
	private PersistenceManager<AccessDefinition> accessPersistenceManager;
	
	// Roles
	
	@Override
	public RoleDefinition createRole(String name, String location, String description) throws AccessException {
		RoleDefinition roleDefinition = new RoleDefinition();
		roleDefinition.setName(name);
		roleDefinition.setLocation(location);
		roleDefinition.setDescription(description);
		roleDefinition.setCreatedBy(UserFacade.getName());
		roleDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public RoleDefinition getRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public boolean existsRole(String name) throws AccessException {
		return getRole(name) != null;
	}
	
	@Override
	public void removeRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public void updateRole(String name, String location, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				RoleDefinition roleDefinition = getRole(name);
				roleDefinition.setLocation(location);
				roleDefinition.setDescription(description);
				rolesPersistenceManager.update(connection, roleDefinition, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	@Override
	public List<RoleDefinition> getRoles() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public RoleDefinition[] parseRoles(String json) {
		return GsonHelper.GSON.fromJson(json, RoleDefinition[].class);
	}
	
	@Override
	public RoleDefinition[] parseRoles(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json)), RoleDefinition[].class);
	}
	
	@Override
	public String serializeRoles(RoleDefinition[] roles) {
		return GsonHelper.GSON.toJson(roles);
	}
	
	// Access
	
	@Override
	public AccessDefinition createAccessDefinition(String location, String uri, String method, String role, String description) throws AccessException {
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setLocation(location);
		accessDefinition.setUri(uri);
		accessDefinition.setMethod(method);
		accessDefinition.setRole(role);
		accessDefinition.setDescription(description);
		accessDefinition.setCreatedBy(UserFacade.getName());
		accessDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				accessPersistenceManager.insert(connection, accessDefinition);
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
	
	@Override
	public AccessDefinition getAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public AccessDefinition getAccessDefinition(String uri, String method, String role) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_URI = ?")
						.where("ACCESS_ROLE = ?")
						.where("ACCESS_METHOD = ?")
						.toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, uri, role, method);
				if (access.isEmpty()) {
					return null;
				}
				if (access.size() > 1) {
					throw new AccessException(format("Security Access duplication for URI [{0}] and Method [{1}] with Role [{2}]", uri, method, role));
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
	
	@Override
	public boolean existsAccessDefinition(String uri, String method, String role) throws AccessException {
		return getAccessDefinition(uri, method, role) != null;
	}
	
	@Override
	public void removeAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	@Override
	public void updateAccessDefinition(long id, String location, String uri, String method, String role, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				AccessDefinition accessDefinition = getAccessDefinition(id);
				accessDefinition.setLocation(location);
				accessDefinition.setUri(uri);
				accessDefinition.setMethod(method);
				accessDefinition.setRole(role);
				accessDefinition.setDescription(description);
				accessPersistenceManager.update(connection, accessDefinition, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	@Override
	public List<AccessDefinition> getAccessDefinitions() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return accessPersistenceManager.findAll(connection, AccessDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	
	@Override
	public List<AccessDefinition> getAccessDefinitionsByUri(String uri) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_URI = ?").toString();
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
	
	@Override
	public List<AccessDefinition> getAccessDefinitionsByUriAndMethod(String uri, String method) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_URI = ?")
						.where(Squle.getNative(connection)
								.expr().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString())
						.toString();
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
	
	@Override
	public boolean isAccessAllowed(String uri, String method, String role) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_URI = ?")
						.where("ACCESS_ROLE = ?")
						.where(Squle.getNative(connection)
								.expr().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString())
						.toString();
				List<AccessDefinition> access = accessPersistenceManager.query(connection, AccessDefinition.class, sql, uri, role, method, AccessDefinition.METHOD_ANY);
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

	@Override
	public List<AccessDefinition> parseAccessDefinitions(String json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}
	
	@Override
	public List<AccessDefinition> parseAccessDefinitions(byte[] json) {
		AccessArtifact accessArtifact = AccessArtifact.parse(json);
		return accessArtifact.divide();
	}
	
	@Override
	public String serializeAccessDefinitions(List<AccessDefinition> accessDefinitions) {
		AccessArtifact accessArtifact = AccessArtifact.combine(accessDefinitions);
		return accessArtifact.serialize();
	}
	
}
