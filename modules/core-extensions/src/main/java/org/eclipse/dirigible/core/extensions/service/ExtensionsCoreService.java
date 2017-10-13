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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

@Singleton
public class ExtensionsCoreService implements IExtensionsCoreService {

	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<ExtensionPointDefinition> extensionPointPersistenceManager;

	@Inject
	private PersistenceManager<ExtensionDefinition> extensionPersistenceManager;

	// Extension Points

	@Override
	public ExtensionPointDefinition createExtensionPoint(String location, String name, String description) throws ExtensionsException {
		ExtensionPointDefinition extensionPointDefinition = new ExtensionPointDefinition();
		extensionPointDefinition.setLocation(location);
		extensionPointDefinition.setName(name);
		extensionPointDefinition.setDescription(description);
		extensionPointDefinition.setCreatedBy(UserFacade.getName());
		extensionPointDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public ExtensionPointDefinition getExtensionPoint(String location) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public ExtensionPointDefinition getExtensionPointByName(String name) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public void removeExtensionPoint(String location) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public void updateExtensionPoint(String location, String name, String description) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				ExtensionPointDefinition extensionPointDefinition = getExtensionPoint(location);
				extensionPointDefinition.setName(name);
				extensionPointDefinition.setDescription(description);
				extensionPointPersistenceManager.update(connection, extensionPointDefinition, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	@Override
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
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public ExtensionDefinition getExtension(String location) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public void removeExtension(String location) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	@Override
	public void updateExtension(String location, String module, String extensionPoint, String description) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				ExtensionDefinition extensionDefinition = getExtension(location);
				extensionDefinition.setModule(module);
				extensionDefinition.setExtensionPoint(extensionPoint);
				extensionDefinition.setDescription(description);
				extensionPersistenceManager.update(connection, extensionDefinition, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	@Override
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

	@Override
	public List<ExtensionDefinition> getExtensionsByExtensionPoint(String extensionPoint) throws ExtensionsException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_EXTENSIONS")
						.where("EXTENSION_EXTENSIONPOINT_NAME = ?").toString();
				List<ExtensionDefinition> extensions = extensionPersistenceManager.query(connection, ExtensionDefinition.class, sql,
						Arrays.asList(extensionPoint));
				if (extensions.isEmpty()) {
					ExtensionPointDefinition extensionPointDefinition = this.getExtensionPointByName(extensionPoint);
					if (extensionPointDefinition == null) {
						throw new ExtensionsException(format("There is no an ExtensionPoint with name [{0}] at all.", extensionPoint));
					}
				}
				return extensions;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ExtensionsException(e);
		}
	}

	@Override
	public boolean existsExtensionPoint(String location) throws ExtensionsException {
		return getExtensionPoint(location) != null;
	}

	@Override
	public boolean existsExtension(String location) throws ExtensionsException {
		return getExtension(location) != null;
	}

	@Override
	public ExtensionPointDefinition parseExtensionPoint(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionPointDefinition.class);
	}

	@Override
	public ExtensionDefinition parseExtension(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionDefinition.class);
	}

	@Override
	public ExtensionPointDefinition parseExtensionPoint(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), ExtensionPointDefinition.class);
	}

	@Override
	public ExtensionDefinition parseExtension(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), ExtensionDefinition.class);
	}

	@Override
	public String serializeExtensionPoint(ExtensionPointDefinition extensionPointDefinition) {
		return GsonHelper.GSON.toJson(extensionPointDefinition);
	}

	@Override
	public String serializeExtension(ExtensionDefinition extensionDefinition) {
		return GsonHelper.GSON.toJson(extensionDefinition);
	}

}
