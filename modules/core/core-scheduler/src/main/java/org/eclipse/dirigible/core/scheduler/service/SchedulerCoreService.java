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
package org.eclipse.dirigible.core.scheduler.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.ICleanupService;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobParameterDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Scheduler Core Service.
 */
public class SchedulerCoreService implements ISchedulerCoreService, ICleanupService {

	private DataSource dataSource = null;

	private PersistenceManager<JobDefinition> jobPersistenceManager = new PersistenceManager<JobDefinition>();
	
	private PersistenceManager<JobLogDefinition> jobLogPersistenceManager = new PersistenceManager<JobLogDefinition>();
	
	private PersistenceManager<JobParameterDefinition> jobParameterPersistenceManager = new PersistenceManager<JobParameterDefinition>();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// Jobs

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createJob(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public JobDefinition createJob(String name, String group, String clazz, String handler, String engine, String description, String expression,
			boolean singleton, Collection<JobParameterDefinition> parameters) throws SchedulerException {
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
		for (JobParameterDefinition parameter : parameters) {
			jobDefinition.addParameter(parameter.getName(), parameter.getType(), parameter.getDefaultValue(), parameter.getChoices(), parameter.getDescription());
		}

		return createOrUpdateJob(jobDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#createOrUpdateJob(org.eclipse.dirigible.core.scheduler.
	 * service.definition.JobDefinition)
	 */
	@Override
	public JobDefinition createOrUpdateJob(JobDefinition jobDefinition) throws SchedulerException {
		if (jobDefinition.getCreatedAt() == null) {
			jobDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		}
		if (jobDefinition.getCreatedBy() == null) {
			jobDefinition.setCreatedBy(UserFacade.getName());
		}
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				JobDefinition existing = getJob(jobDefinition.getName());
				if (existing != null) {
					jobPersistenceManager.update(connection, jobDefinition);
					createOrUpdateParameters(connection, jobDefinition);
				} else {
					jobPersistenceManager.insert(connection, jobDefinition);
					createOrUpdateParameters(connection, jobDefinition);
				}
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

	private void createOrUpdateParameters(Connection connection, JobDefinition jobDefinition) {
		for (JobParameterDefinition parameter : jobDefinition.getParameters()) {
			JobParameterDefinition existingParameter = jobParameterPersistenceManager.find(connection, JobParameterDefinition.class, parameter.getId());
			if (existingParameter == null) {
				jobParameterPersistenceManager.insert(connection, parameter);
			} else {
				jobParameterPersistenceManager.update(connection, parameter);
			}
		}
		jobParameterPersistenceManager.tableCheck(connection, JobParameterDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_JOB_PARAMETERS").where("JOBPARAM_JOB_NAME = ?").toString();
		List<JobParameterDefinition> parameters = jobParameterPersistenceManager.query(connection, JobParameterDefinition.class, sql,
				Arrays.asList(jobDefinition.getName()));
		for (JobParameterDefinition parameter : parameters) {
			boolean exists = false;
			for (JobParameterDefinition existing : jobDefinition.getParameters()) {
				if (existing.getName().equals(parameter.getName())) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				jobParameterPersistenceManager.delete(connection, JobParameterDefinition.class, parameter.getId());
			}
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
				connection = getDataSource().getConnection();
				JobDefinition jobDefinition = jobPersistenceManager.find(connection, JobDefinition.class, name);
				if (jobDefinition == null) {
					return null;
				}
				jobParameterPersistenceManager.tableCheck(connection, JobParameterDefinition.class);
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_JOB_PARAMETERS").where("JOBPARAM_JOB_NAME = ?").toString();
				List<JobParameterDefinition> parameters = jobParameterPersistenceManager.query(connection, JobParameterDefinition.class, sql,
						Arrays.asList(jobDefinition.getName()));
				for (JobParameterDefinition parameter : parameters) {
					jobDefinition.addParameter(parameter.getName(), parameter.getType(), parameter.getDefaultValue(), parameter.getChoices(), parameter.getDescription());
				}
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
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#removeJob(java.lang.String)
	 */
	@Override
	public void removeJob(String name) throws SchedulerException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
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
			boolean singleton, Collection<JobParameterDefinition> parameters) throws SchedulerException {
		
		JobDefinition jobDefinition = getJob(name);
		jobDefinition.setGroup(group);
		jobDefinition.setClazz(clazz);
		jobDefinition.setHandler(handler);
		jobDefinition.setEngine(engine);
		jobDefinition.setDescription(description);
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(singleton);
		for (JobParameterDefinition parameter : parameters) {
			jobDefinition.addParameter(parameter.getName(), parameter.getType(), parameter.getDefaultValue(), parameter.getChoices(), parameter.getDescription());
		}
		createOrUpdateJob(jobDefinition);
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
				connection = getDataSource().getConnection();
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
		for (JobParameterDefinition parameter : jobDefinition.getParameters()) {
			parameter.setId(jobDefinition.getName(), parameter.getName());
			parameter.setJobName(jobDefinition.getName());
		}
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
	
	// Job Log
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobTriggered(java.lang.String, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobTriggered(String name, String handler) throws SchedulerException {
		JobLogDefinition jobLogDefinition = new JobLogDefinition();
		jobLogDefinition.setName(name);
		jobLogDefinition.setHandler(handler);
		jobLogDefinition.setStatus(JobLogDefinition.JOB_LOG_STATUS_TRIGGRED);
		jobLogDefinition.setTriggeredAt(new Timestamp(new java.util.Date().getTime()));
		return registerJobLog(jobLogDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobFinished(java.lang.String, java.lang.String,
	 * int, java.util.Date)
	 */
	@Override
	public JobLogDefinition jobFinished(String name, String handler, long triggeredId, java.util.Date triggeredAt) throws SchedulerException {
		JobLogDefinition jobLogDefinition = new JobLogDefinition();
		jobLogDefinition.setName(name);
		jobLogDefinition.setHandler(handler);
		jobLogDefinition.setStatus(JobLogDefinition.JOB_LOG_STATUS_FINISHED);
		jobLogDefinition.setTriggeredId(triggeredId);
		jobLogDefinition.setTriggeredAt(new Timestamp(triggeredAt.getTime()));
		jobLogDefinition.setFinishedAt(new Timestamp(new java.util.Date().getTime()));
		return registerJobLog(jobLogDefinition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobFailed(java.lang.String, java.lang.String,
	 * int, java.util.Date, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobFailed(String name, String handler, long triggeredId, java.util.Date triggeredAt, String message) throws SchedulerException {
		JobLogDefinition jobLogDefinition = new JobLogDefinition();
		jobLogDefinition.setName(name);
		jobLogDefinition.setHandler(handler);
		jobLogDefinition.setStatus(JobLogDefinition.JOB_LOG_STATUS_FAILED);
		jobLogDefinition.setTriggeredId(triggeredId);
		jobLogDefinition.setTriggeredAt(new Timestamp(triggeredAt.getTime()));
		jobLogDefinition.setFinishedAt(new Timestamp(new java.util.Date().getTime()));
		jobLogDefinition.setMessage(message);
		return registerJobLog(jobLogDefinition);
	}
	
	private JobLogDefinition registerJobLog(JobLogDefinition jobLogDefinition) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				jobLogPersistenceManager.insert(connection, jobLogDefinition);
				return jobLogDefinition;
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobLogs(java.lang.String)
	 */
	@Override
	public List<JobLogDefinition> getJobLogs(String name) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_JOB_LOGS")
						.where("JOBLOG_NAME = ?").toString();
				return jobLogPersistenceManager.query(connection, JobLogDefinition.class, sql, Arrays.asList(name));
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#deleteOldJobLogs()
	 */
	@Override
	public void deleteOldJobLogs() throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_JOB_LOGS")
						.where("JOBLOG_TRIGGERED_AT < ?")
						.build();
				jobLogPersistenceManager.tableCheck(connection, JobLogDefinition.class);
				jobLogPersistenceManager.execute(connection, sql, new Timestamp(System.currentTimeMillis() - 7*24*60*60*1000)); // older than a week
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	@Override
	public void cleanup() {
		try {
			deleteOldJobLogs();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobParameters(java.lang.String)
	 */
	@Override
	public List<JobParameterDefinition> getJobParameters(String name) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_JOB_PARAMETERS")
						.where("JOBPARAM_JOB_NAME = ?").toString();
				return jobParameterPersistenceManager.query(connection, JobParameterDefinition.class, sql, Arrays.asList(name));
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

}
