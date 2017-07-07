package org.eclipse.dirigible.core.security.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
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
	
	boolean rolesTableExists = false;
	
	boolean accessTableExists = false;
	
	// Roles
	
	@Override
	public RoleDefinition createRole(String name, String description) throws AccessException {
		RoleDefinition roleDefinition = new RoleDefinition();
		roleDefinition.setName(name);
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
	public void updateRole(String name, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				RoleDefinition roleDefinition = getRole(name);
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
	
	// Access
	
	@Override
	public AccessDefinition createAccessDefinition(String location, String method, String role, String description) throws AccessException {
		AccessDefinition accessDefinition = new AccessDefinition();
		accessDefinition.setLocation(location);
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
	public void updateAccessDefinition(long id, String location, String method, String role, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				AccessDefinition accessDefinition = getAccessDefinition(id);
				accessDefinition.setLocation(location);
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
	public List<AccessDefinition> getAccessDefinitionsByLocation(String location) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_LOCATION = ?").toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, Arrays.asList(location));
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
	public List<AccessDefinition> getAccessDefinitionsByLocationAndMethod(String location, String method) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_LOCATION = ?")
						.where(Squle.getNative(connection)
								.expr().and("ACCESS_METHOD = ?").or("ACCESS_METHOD = ?").toString())
						.toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, location, method, AccessDefinition.METHOD_ANY);
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
