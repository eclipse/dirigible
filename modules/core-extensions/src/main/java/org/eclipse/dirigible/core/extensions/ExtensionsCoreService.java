package org.eclipse.dirigible.core.extensions;

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
public class ExtensionsCoreService implements IExtensionsCoreService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private PersistenceManager<ExtensionPointDefinition> extensionPointPersistenceManager;
	
	@Inject
	private PersistenceManager<ExtensionDefinition> extensionPersistenceManager;
	
	// Extension Points
	
	public void createExtensionPoint(String extensionPoint, String description) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = new ExtensionPointDefinition();
		extensionPointDefinition.setLocation(extensionPoint);
		extensionPointDefinition.setDescription(description);
		extensionPointDefinition.setCreatedBy(UserFacade.getName());
		extensionPointDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				extensionPointPersistenceManager.insert(connection, extensionPointDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public ExtensionPointDefinition getExtensionPoint(String extensionPoint) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return extensionPointPersistenceManager.find(connection, ExtensionPointDefinition.class, extensionPoint);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public void removeExtensionPoint(String extensionPoint) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				extensionPointPersistenceManager.delete(connection, ExtensionPointDefinition.class, extensionPoint);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public void updateExtensionPoint(String extensionPoint, String description) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				ExtensionPointDefinition extensionPointDefinition = getExtensionPoint(extensionPoint);
				extensionPointDefinition.setDescription(description);
				extensionPointPersistenceManager.update(connection, extensionPointDefinition, extensionPoint);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	public void createExtension(String extension, String extensionPoint, String description) throws ExtensionsException {
		ExtensionDefinition extensionDefinition = new ExtensionDefinition();
		extensionDefinition.setLocation(extension);
		extensionDefinition.setExtensionPoint(extensionPoint);
		extensionDefinition.setDescription(description);
		extensionDefinition.setCreatedBy(UserFacade.getName());
		extensionDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				extensionPersistenceManager.insert(connection, extensionDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public ExtensionDefinition getExtension(String extension) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return extensionPersistenceManager.find(connection, ExtensionDefinition.class, extension);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public void removeExtension(String extension) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				extensionPersistenceManager.delete(connection, ExtensionDefinition.class, extension);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public void updateExtension(String extension, String extensionPoint, String description) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				ExtensionDefinition extensionDefinition = getExtension(extension);
				extensionDefinition.setExtensionPoint(extensionPoint);
				extensionDefinition.setDescription(description);
				extensionPersistenceManager.update(connection, extensionDefinition, extension);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
	public List<ExtensionDefinition> getExtensions() throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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
	
	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = Squle.getNative(connection)
						.select()
						.column("*")
						.from("DIRIGIBLE_EXTENSIONS")
						.where("EXTENSION_EXTENSIONPOINT_LOCATION = ?").toString();
				return extensionPersistenceManager.query(connection, ExtensionDefinition.class, sql, Arrays.asList(extensionPoint));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}
	
}
