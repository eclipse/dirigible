/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class SchedulerCoreService.
 */
@Singleton
public class SchedulerCoreService implements ISchedulerCoreService {

	/** The data source. */
	@Inject
	private DataSource dataSource;

	/** The job persistence manager. */
	@Inject
	private PersistenceManager<JobDefinition> jobPersistenceManager;

	// Jobs

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createJob(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public JobDefinition createJob(String name, String group, String clazz, String description, String expression, boolean singleton)
			throws SchedulerException {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(name);
		jobDefinition.setGroup(group);
		jobDefinition.setClazz(clazz);
		jobDefinition.setDescription(description);
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(singleton);
		jobDefinition.setCreatedBy(UserFacade.getName());
		jobDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		return createJob(jobDefinition);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createJob(org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition)
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
			Connection connection = dataSource.getConnection();
			try {
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

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJob(java.lang.String)
	 */
	@Override
	public JobDefinition getJob(String name) throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#removeJob(java.lang.String)
	 */
	@Override
	public void removeJob(String name) throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#updateJob(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void updateJob(String name, String group, String clazz, String description, String expression, boolean singleton)
			throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
				JobDefinition jobDefinition = getJob(name);
				jobDefinition.setGroup(group);
				jobDefinition.setClazz(clazz);
				jobDefinition.setDescription(description);
				jobDefinition.setExpression(expression);
				jobDefinition.setSingleton(singleton);
				jobPersistenceManager.update(connection, jobDefinition, name);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobs()
	 */
	@Override
	public List<JobDefinition> getJobs() throws SchedulerException {
		try {
			Connection connection = dataSource.getConnection();
			try {
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

}
