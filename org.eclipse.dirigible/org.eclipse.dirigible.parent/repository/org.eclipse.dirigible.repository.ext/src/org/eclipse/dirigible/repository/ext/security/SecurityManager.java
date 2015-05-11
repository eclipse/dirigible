/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class SecurityManager {

	private static final Logger logger = Logger.getLogger(SecurityManager.class);
	
	private static final String LOCATION_S_AND_S_DOES_NOT_EXIST = Messages.getString("SecurityManager.LOCATION_S_AND_S_DOES_NOT_EXIST"); //$NON-NLS-1$

	private static final String LOCATION_S_DOES_NOT_EXIST = Messages.getString("SecurityManager.LOCATION_S_DOES_NOT_EXIST"); //$NON-NLS-1$

	private static final String DATABASE_ERROR = Messages.getString("SecurityManager.DATABASE_ERROR"); //$NON-NLS-1$

	private static final String EVERYONE_ROLE = "Everyone"; //$NON-NLS-1$

	private static final String INSERT_ACCESS = "/org/eclipse/dirigible/repository/ext/security/sql/insert_access.sql"; //$NON-NLS-1$

	private static final String GET_ROLES_BY_LOCATION = "/org/eclipse/dirigible/repository/ext/security/sql/get_roles_by_location.sql"; //$NON-NLS-1$

	private static final String GET_ROLES_BY_LOCATION_AND_ROLE = "/org/eclipse/dirigible/repository/ext/security/sql/get_roles_by_location_and_role.sql"; //$NON-NLS-1$

	private static final String GET_ACCESS_LOCATIONS = "/org/eclipse/dirigible/repository/ext/security/sql/get_access_locations.sql"; //$NON-NLS-1$

	private static final String REMOVE_BY_LOCATION = "/org/eclipse/dirigible/repository/ext/security/sql/remove_by_location.sql"; //$NON-NLS-1$

	private static final String REMOVE_BY_LOCATION_AND_ROLE = "/org/eclipse/dirigible/repository/ext/security/sql/remove_by_location_and_role.sql"; //$NON-NLS-1$

	private static final String GET_ACCESS_LIST = "/org/eclipse/dirigible/repository/ext/security/sql/get_access_list.sql"; //$NON-NLS-1$

	private static SecurityManager instance;

	private DataSource dataSource;

	private IRepository repository;

	private DBUtils dbUtils;

	public static SecurityManager getInstance(IRepository repository,
			DataSource dataSource) {
		if (instance == null) {
			instance = new SecurityManager(repository, dataSource);
		}
		return instance;
	}

	public SecurityManager(IRepository repository, DataSource dataSource) {
		this.dataSource = dataSource;
		this.repository = repository;
		this.dbUtils = new DBUtils(dataSource);
	}

	public IRepository getRepository() {
		return this.repository;
	}

	public DBUtils getDBUtils() {
		return this.dbUtils;
	}

	public List<String> getSecuredLocations() throws SecurityException {
		List<String> securedLocations = null;

		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			securedLocations = getSecuredLocations(connection);

		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}

		return securedLocations;
	}

	public List<SecurityLocationMetadata> getAccessList()
			throws SecurityException {
		List<SecurityLocationMetadata> securedLocations = null;

		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			securedLocations = getAccessList(connection);

		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}

		return securedLocations;
	}

	public void secureLocation(String location, HttpServletRequest request)
			throws SecurityException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (!isSecuredLocationInternal(connection, location)) {
				insertLocation(connection, location, null, request);
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}
	}

	public void secureLocationWithRole(String location, String roleName,
			HttpServletRequest request) throws SecurityException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (!isSecuredLocationInternal(connection, location, roleName)) {
				insertLocation(connection, location, roleName, request);
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}
	}

	public void unsecureLocation(String location) throws SecurityException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (isSecuredLocationInternal(connection, location)) {
				removeLocation(connection, location);
			} else {
				throw new SecurityException(String.format(
						LOCATION_S_DOES_NOT_EXIST, location));
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}
	}

	public void unsecureLocationForRole(String location, String roleName)
			throws SecurityException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (isSecuredLocationInternal(connection, location, roleName)) {
				removeLocationWithRole(connection, location, roleName);
			} else {
				throw new SecurityException(String.format(
						LOCATION_S_AND_S_DOES_NOT_EXIST, location, roleName));
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(DATABASE_ERROR, e);
			}
		}
	}

	private List<String> getSecuredLocations(Connection connection)
			throws SQLException, IOException {
		List<String> securedLocations = new ArrayList<String>();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String script = getDBUtils().readScript(connection,
					GET_ACCESS_LOCATIONS, this.getClass());
			ResultSet resultSet = statement.executeQuery(script);
			while (resultSet.next()) {
				securedLocations.add(resultSet.getString(1));
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return securedLocations;
	}

	private List<SecurityLocationMetadata> getAccessList(Connection connection)
			throws SQLException, IOException {
		List<SecurityLocationMetadata> securedLocations = new ArrayList<SecurityLocationMetadata>();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String script = getDBUtils().readScript(connection,
					GET_ACCESS_LIST, this.getClass());
			ResultSet resultSet = statement.executeQuery(script);
			SecurityLocationMetadata securityLocationMetadata = new SecurityLocationMetadata();
			while (resultSet.next()) {
				String location = resultSet.getString(1);
				if (location == null) {
					continue;
				}
				if (!location.equals(securityLocationMetadata.getLocation())) {
					if (securityLocationMetadata.getLocation() != null) {
						securedLocations.add(securityLocationMetadata);
					}
					securityLocationMetadata = new SecurityLocationMetadata();
					securityLocationMetadata.setLocation(location);
				}
				securityLocationMetadata.getRoles().add(resultSet.getString(2));
			}
			securedLocations.add(securityLocationMetadata);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return securedLocations;
	}

	public boolean isSecuredLocation(String location)
			throws SQLException, IOException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			return isSecuredLocationInternal(connection, location);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	private boolean isSecuredLocationInternal(Connection connection, String location)
			throws SQLException, IOException {
		PreparedStatement statement = null;
		try {
			String script = getDBUtils().readScript(connection,
					GET_ROLES_BY_LOCATION, this.getClass());
			statement = connection.prepareStatement(script);
			statement.setString(1, location + "%"); //$NON-NLS-1$
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return false;
	}

	public boolean isSecuredLocation(String location, String roleName)
			throws SQLException, IOException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			return isSecuredLocationInternal(connection, location, roleName);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public boolean isSecuredLocationInternal(Connection connection, String location,
			String roleName) throws SQLException, IOException {
		PreparedStatement statement = null;
		try {
			String script = getDBUtils().readScript(connection,
					GET_ROLES_BY_LOCATION_AND_ROLE, this.getClass());
			statement = connection.prepareStatement(script);
			statement.setString(1, location + "%"); //$NON-NLS-1$
			statement.setString(2, roleName);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return false;
	}

	public List<String> getRolesForLocation(String location)
			throws SQLException, IOException {
		List<String> securedRoles = new ArrayList<String>();
		Connection connection = dataSource.getConnection();
		try {
				String script = getDBUtils().readScript(connection,
						GET_ROLES_BY_LOCATION, this.getClass());
				
				PreparedStatement statement = null;
				try {
					statement = connection.prepareStatement(script);
					
					RepositoryPath path = new RepositoryPath(location);
					for (int i = path.getSegments().length; i > 0; i--) {
						
						String transitiveLocation = path.constructPath(i);
					
							statement.setString(1, transitiveLocation);
							ResultSet resultSet = statement.executeQuery();
							while (resultSet.next()) {
								securedRoles.add(resultSet.getString(1));
							}
							
							if (securedRoles.size() > 0) {
								break;
							}
						
					}
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return securedRoles;
	}

	private void insertLocation(Connection connection, String location,
			String roleName, HttpServletRequest request) throws SQLException, IOException {
		PreparedStatement statement = null;
		try {
			String script = getDBUtils().readScript(connection, INSERT_ACCESS,
					this.getClass());
			statement = connection.prepareStatement(script);
			statement.setString(1, location);
			if (roleName == null) {
				statement.setString(2, EVERYONE_ROLE);
			} else {
				statement.setString(2, roleName);
			}
			statement.setString(3, RequestUtils.getUser(request));

			statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	private void removeLocation(Connection connection, String location)
			throws SQLException, IOException {
		PreparedStatement statement = null;
		try {
			String script = getDBUtils().readScript(connection,
					REMOVE_BY_LOCATION, this.getClass());
			statement = connection.prepareStatement(script);
			statement.setString(1, location);
			statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	private void removeLocationWithRole(Connection connection, String location,
			String roleName) throws SQLException, IOException {

		if (roleName == null) {
			removeLocation(connection, location);
			return;
		}

		PreparedStatement statement = null;
		try {
			String script = getDBUtils().readScript(connection,
					REMOVE_BY_LOCATION_AND_ROLE, this.getClass());
			statement = connection.prepareStatement(script);
			statement.setString(1, location);
			statement.setString(2, roleName);
			statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

}
