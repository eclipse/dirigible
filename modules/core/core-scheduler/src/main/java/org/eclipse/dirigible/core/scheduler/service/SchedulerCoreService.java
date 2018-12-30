/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.scheduler.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;

/**
 * The Scheduler Core Service.
 */
@Singleton
public class SchedulerCoreService implements ISchedulerCoreService {

	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<JobDefinition> jobPersistenceManager;

	// Jobs

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createJob(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public JobDefinition createJob(String name, String group, String clazz, String handler, String engine, String description, String expression,
			boolean singleton) throws SchedulerException {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(name);
		jobDefinition.setGroup(group);
		jobDefinition.setClazz(clazz);
		jobDefinition.setHandler(handler);
		jobDefinition.setEngine(engine);
		jobDefinition.setDescription(description);
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(singleton);
		jobDefinition.setCreatedBy(UserFacade.getName());
		jobDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		return createJob(jobDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createJob(org.eclipse.dirigible.core.scheduler.
	 * service.definition.JobDefinition)
	 */
	@Override
	public JobDefinition createJob(JobDefinition jobDefinition) throws SchedulerException {
		if (jobDefinition.getCreatedAt() == null) {
			jobDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		}
		if (jobDefinition.getCreatedBy() == null) {
			jobDefinition.setCreatedBy(UserFacade.getName());
		}
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				jobPersistenceManager.insert(connection, jobDefinition);
				return jobDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJob(java.lang.String)
	 */
	@Override
	public JobDefinition getJob(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return jobPersistenceManager.find(connection, JobDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#removeJob(java.lang.String)
	 */
	@Override
	public void removeJob(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				jobPersistenceManager.delete(connection, JobDefinition.class, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#updateJob(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void updateJob(String name, String group, String clazz, String handler, String engine, String description, String expression,
			boolean singleton) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				JobDefinition jobDefinition = getJob(name);
				jobDefinition.setGroup(group);
				jobDefinition.setClazz(clazz);
				jobDefinition.setHandler(handler);
				jobDefinition.setEngine(engine);
				jobDefinition.setDescription(description);
				jobDefinition.setExpression(expression);
				jobDefinition.setSingleton(singleton);
				jobPersistenceManager.update(connection, jobDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobs()
	 */
	@Override
	public List<JobDefinition> getJobs() throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return jobPersistenceManager.findAll(connection, JobDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#existsJob(java.lang.String)
	 */
	@Override
	public boolean existsJob(String name) throws SchedulerException {
		return getJob(name) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#parseJob(java.lang.String)
	 */
	@Override
	public JobDefinition parseJob(String json) {
		JobDefinition jobDefinition = GsonHelper.GSON.fromJson(json, JobDefinition.class);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_DEFINED);
		return jobDefinition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#parseJob(byte[])
	 */
	@Override
	public JobDefinition parseJob(byte[] content) {
		JobDefinition jobDefinition = GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8),
				JobDefinition.class);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_DEFINED);
		return jobDefinition;
	}

	@Override
	public String serializeJob(JobDefinition jobDefinition) {
		return GsonHelper.GSON.toJson(jobDefinition);
	}

}
