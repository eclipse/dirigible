package org.eclipse.dirigible.core.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#createRole(java.lang.String, java.lang.String)
	 */
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
				checkRolesTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getRole(java.lang.String)
	 */
	@Override
	public RoleDefinition getRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#removeRole(java.lang.String)
	 */
	@Override
	public void removeRole(String name) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#updateRole(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateRole(String name, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getRoles()
	 */
	@Override
	public List<RoleDefinition> getRoles() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkRolesTable(connection);
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
	
	private void checkRolesTable(Connection connection) {
		if (!rolesTableExists) {
			if (!rolesPersistenceManager.tableExists(connection, RoleDefinition.class)) {
				rolesPersistenceManager.tableCreate(connection, RoleDefinition.class);				
			}
			rolesTableExists = true;
		}
	}
	
	
	// Access
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#createAccessDefinition(java.lang.String, java.lang.String, java.lang.String)
	 */
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
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getAccessDefinition(long)
	 */
	@Override
	public AccessDefinition getAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#removeAccessDefinition(long)
	 */
	@Override
	public void removeAccessDefinition(long id) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#updateAccessDefinition(long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateAccessDefinition(long id, String location, String method, String role, String description) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getAccessDefinitions()
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitions() throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getAccessDefinitionsByLocation(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByLocation(String location) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.security.ISecurityCoreService#getAccessDefinitionsByLocation(java.lang.String)
	 */
	@Override
	public List<AccessDefinition> getAccessDefinitionsByLocationAndMethod(String location, String method) throws AccessException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkAccessTable(connection);
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_SECURITY_ACCESS")
						.where("ACCESS_LOCATION = ?")
						.where("ACCESS_METHOD = ?").toString();
				return accessPersistenceManager.query(connection, AccessDefinition.class, sql, Arrays.asList(location, method));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new AccessException(e);
		}
	}
	

	private void checkAccessTable(Connection connection) {
		if (!accessTableExists) {
			if (!accessPersistenceManager.tableExists(connection, AccessDefinition.class)) {
				accessPersistenceManager.tableCreate(connection, AccessDefinition.class);				
			}
			accessTableExists = true;
		}
	}

}
