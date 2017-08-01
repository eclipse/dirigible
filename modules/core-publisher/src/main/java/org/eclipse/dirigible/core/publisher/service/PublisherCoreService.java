package org.eclipse.dirigible.core.publisher.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.publisher.api.IPublisherCoreService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

@Singleton
public class PublisherCoreService implements IPublisherCoreService {

	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<PublishRequestDefinition> publishRequestPersistenceManager;

	@Inject
	private PersistenceManager<PublishLogDefinition> publishLogPersistenceManager;

	// Publish Request

	@Override
	public PublishRequestDefinition createPublishRequest(String workspace, String path, String registry) throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = new PublishRequestDefinition();
		publishRequestDefinition.setWorkspace(workspace);
		publishRequestDefinition.setPath(path);
		publishRequestDefinition.setRegistry(registry);
		publishRequestDefinition.setCreatedBy(UserFacade.getName());
		publishRequestDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = dataSource.getConnection();
			try {
				publishRequestPersistenceManager.insert(connection, publishRequestDefinition);
				return publishRequestDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public PublishRequestDefinition createPublishRequest(String workspace, String path) throws PublisherException {
		return createPublishRequest(workspace, path, IRepositoryStructure.PATH_REGISTRY);
	}

	@Override
	public PublishRequestDefinition getPublishRequest(long id) throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return publishRequestPersistenceManager.find(connection, PublishRequestDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public void removePublishRequest(long id) throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				publishRequestPersistenceManager.delete(connection, PublishRequestDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public List<PublishRequestDefinition> getPublishRequests() throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return publishRequestPersistenceManager.findAll(connection, PublishRequestDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	// Publish Log

	@Override
	public PublishLogDefinition createPublishLog(String source, String target) throws PublisherException {
		PublishLogDefinition publishLogDefinition = new PublishLogDefinition();
		publishLogDefinition.setSource(source);
		publishLogDefinition.setTarget(target);
		publishLogDefinition.setCreatedBy(UserFacade.getName());
		publishLogDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = dataSource.getConnection();
			try {
				publishLogPersistenceManager.insert(connection, publishLogDefinition);
				return publishLogDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public PublishLogDefinition getPublishLog(long id) throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return publishLogPersistenceManager.find(connection, PublishLogDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public void removePublishLog(long id) throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				publishLogPersistenceManager.delete(connection, PublishLogDefinition.class, id);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public List<PublishLogDefinition> getPublishLogs() throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				return publishLogPersistenceManager.findAll(connection, PublishLogDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public List<PublishRequestDefinition> getPublishRequestsAfter(Timestamp timestamp) throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {

				String sql = Squle.getNative(connection).select().column("*").from("DIRIGIBLE_PUBLISH_REQUESTS").where("PUBREQ_CREATED_AT > ?")
						.toString();
				Timestamp latest = (timestamp == null) ? new Timestamp(0) : timestamp;
				return publishRequestPersistenceManager.query(connection, PublishRequestDefinition.class, sql, latest);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	@Override
	public Timestamp getLatestPublishLog() throws PublisherException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				publishRequestPersistenceManager.tableCheck(connection, PublishLogDefinition.class);
				Timestamp date = new Timestamp(new java.util.Date().getTime());
				String sql = Squle.getNative(connection).select().column("MAX(PUBLOG_CREATED_AT)").from("DIRIGIBLE_PUBLISH_LOGS").toString();

				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);
				if (resultSet.next()) {
					date = resultSet.getTimestamp(1);
				}
				return (date != null ? date : new Timestamp(0));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

}
