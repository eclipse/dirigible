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
package org.eclipse.dirigible.core.publisher.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.ICleanupService;
import org.eclipse.dirigible.commons.config.ResourcesCache;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.publisher.api.IPublisherCoreService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

/**
 * The PublisherCoreService implementation managing the requests for publish artifacts.
 */
public class PublisherCoreService implements IPublisherCoreService, ICleanupService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The publish request persistence manager. */
	private PersistenceManager<PublishRequestDefinition> publishRequestPersistenceManager = new PersistenceManager<PublishRequestDefinition>();

	/** The publish log persistence manager. */
	private PersistenceManager<PublishLogDefinition> publishLogPersistenceManager = new PersistenceManager<PublishLogDefinition>();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// Publish Request

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#createPublishRequest(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PublishRequestDefinition createPublishRequest(String workspace, String path, String registry) throws PublisherException {
		ResourcesCache.clear();
		PublishRequestDefinition publishRequestDefinition = new PublishRequestDefinition();
		publishRequestDefinition.setWorkspace(workspace);
		publishRequestDefinition.setPath(path);
		publishRequestDefinition.setRegistry(registry);
		publishRequestDefinition.setCreatedBy(UserFacade.getName());
		publishRequestDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#createPublishRequest(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PublishRequestDefinition createUnpublishRequest(String workspace, String path, String registry) throws PublisherException {
		ResourcesCache.clear();
		PublishRequestDefinition publishRequestDefinition = new PublishRequestDefinition();
		publishRequestDefinition.setWorkspace(workspace);
		publishRequestDefinition.setPath(path);
		publishRequestDefinition.setRegistry(registry);
		publishRequestDefinition.setCommand(PublishRequestDefinition.COMMAND_UNPUBLISH);
		publishRequestDefinition.setCreatedBy(UserFacade.getName());
		publishRequestDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#createPublishRequest(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public PublishRequestDefinition createPublishRequest(String workspace, String path) throws PublisherException {
		return createPublishRequest(workspace, path, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#createUnpublishRequest(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public PublishRequestDefinition createUnpublishRequest(String workspace, String path) throws PublisherException {
		return createUnpublishRequest(workspace, path, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getPublishRequest(long)
	 */
	@Override
	public PublishRequestDefinition getPublishRequest(long id) throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#removePublishRequest(long)
	 */
	@Override
	public void removePublishRequest(long id) throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#removeAllPublishRequests()
	 */
	@Override
	public void removeAllPublishRequests() throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				publishRequestPersistenceManager.tableCheck(connection, PublishRequestDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PUBLISH_REQUESTS").toString();
				publishRequestPersistenceManager.tableCheck(connection, PublishRequestDefinition.class);
				publishRequestPersistenceManager.execute(connection, sql);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getPublishRequests()
	 */
	@Override
	public List<PublishRequestDefinition> getPublishRequests() throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#deleteOldPublishRequests()
	 */
	@Override
	public void deleteOldPublishRequests() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				publishRequestPersistenceManager.tableCheck(connection, PublishRequestDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PUBLISH_REQUESTS")
						.where("PUBREQ_CREATED_AT < ?")
						.build();
				publishRequestPersistenceManager.execute(connection, sql, new Timestamp(System.currentTimeMillis() - 60*60*1000)); // older than an hour
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	// Publish Log

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#createPublishLog(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public PublishLogDefinition createPublishLog(String source, String target) throws PublisherException {
		PublishLogDefinition publishLogDefinition = new PublishLogDefinition();
		publishLogDefinition.setSource(source);
		publishLogDefinition.setTarget(target);
		publishLogDefinition.setCreatedBy(UserFacade.getName());
		publishLogDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getPublishLog(long)
	 */
	@Override
	public PublishLogDefinition getPublishLog(long id) throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#removePublishLog(long)
	 */
	@Override
	public void removePublishLog(long id) throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getPublishLogs()
	 */
	@Override
	public List<PublishLogDefinition> getPublishLogs() throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getPublishRequestsAfter(java.sql.Timestamp)
	 */
	@Override
	public List<PublishRequestDefinition> getPublishRequestsAfter(Timestamp timestamp) throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PUBLISH_REQUESTS").where("PUBREQ_CREATED_AT > ?")
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#getLatestPublishLog()
	 */
	@Override
	public Timestamp getLatestPublishLog() throws PublisherException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				publishLogPersistenceManager.tableCheck(connection, PublishLogDefinition.class);
				Timestamp date = new Timestamp(new java.util.Date().getTime());
				String sql = SqlFactory.getNative(connection).select().column("MAX(PUBLOG_CREATED_AT)").from("DIRIGIBLE_PUBLISH_LOGS").toString();

				PreparedStatement statement = null;
				try {
					statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						date = resultSet.getTimestamp(1);
					}
					return (date != null ? date : new Timestamp(0));
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
		} catch (SQLException e) {
			throw new PublisherException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.publisher.api.IPublisherCoreService#deleteOldPublishLogs()
	 */
	@Override
	public void deleteOldPublishLogs() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				publishLogPersistenceManager.tableCheck(connection, PublishLogDefinition.class);
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PUBLISH_LOGS")
						.where("PUBLOG_CREATED_AT < ?")
						.build();
				publishLogPersistenceManager.execute(connection, sql, new Timestamp(System.currentTimeMillis() - 60*60*1000)); // older than an hour
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	

	@Override
	public void cleanup() {
		try {
			deleteOldPublishRequests();
			deleteOldPublishLogs();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
