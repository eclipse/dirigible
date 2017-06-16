package org.eclipse.dirigible.core.extensions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.database.persistence.PersistenceManager;

@Singleton
public class ExtensionsCoreService implements ICoreService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private PersistenceManager<ExtensionPointDefinition> extensionPointPersistenceManager;
	
	@Inject
	private PersistenceManager<ExtensionPointDefinition> extensionPersistenceManager;
	
	boolean extensionPointsTableExists = false;
	
	public void createExtensionPoint(String extensionPoint, String description) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = new ExtensionPointDefinition();
		extensionPointDefinition.setLocation(extensionPoint);
		extensionPointDefinition.setDescription(description);
		extensionPointDefinition.setCreatedBy(UserFacade.getName());
		extensionPointDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkExtensionPointTable(connection);
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
	
	public List<ExtensionPointDefinition> getExtensionPoints() throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				checkExtensionPointTable(connection);
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

	private void checkExtensionPointTable(Connection connection) {
		if (!extensionPointsTableExists) {
			if (!extensionPointPersistenceManager.existsTable(connection, ExtensionPointDefinition.class)) {
				extensionPointPersistenceManager.createTable(connection, ExtensionPointDefinition.class);				
			}
			extensionPointsTableExists = true;
		}
	}

}
