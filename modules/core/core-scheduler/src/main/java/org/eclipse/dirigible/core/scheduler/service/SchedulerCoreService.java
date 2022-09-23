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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.dirigible.api.v3.mail.MailFacade;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.ICleanupService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.generation.api.GenerationEnginesManager;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
//import org.eclipse.dirigible.api.v3.platform.RegistryFacade;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobEmailDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobLogDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobParameterDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.engine.api.resource.RegistryResourceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Scheduler Core Service.
 */
public class SchedulerCoreService implements ISchedulerCoreService, ICleanupService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SchedulerCoreService.class);

	/** The data source. */
	private DataSource dataSource = null;

	/** The job persistence manager. */
	private PersistenceManager<JobDefinition> jobPersistenceManager = new PersistenceManager<JobDefinition>();
	
	/** The job log persistence manager. */
	private PersistenceManager<JobLogDefinition> jobLogPersistenceManager = new PersistenceManager<JobLogDefinition>();
	
	/** The job parameter persistence manager. */
	private PersistenceManager<JobParameterDefinition> jobParameterPersistenceManager = new PersistenceManager<JobParameterDefinition>();

	/** The job email persistence manager. */
	private PersistenceManager<JobEmailDefinition> jobEmailPersistenceManager = new PersistenceManager<JobEmailDefinition>();
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}
	
	/** The Constant DIRIGIBLE_SCHEDULER_LOGS_RETENTION_PERIOD. */
	private static final String DIRIGIBLE_SCHEDULER_LOGS_RETENTION_PERIOD = "DIRIGIBLE_SCHEDULER_LOGS_RETENTION_PERIOD";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_SENDER. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_SENDER = "DIRIGIBLE_SCHEDULER_EMAIL_SENDER";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_RECIPIENTS. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_RECIPIENTS = "DIRIGIBLE_SCHEDULER_EMAIL_RECIPIENTS";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ERROR. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ERROR = "DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ERROR";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_NORMAL. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_NORMAL = "DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_NORMAL";

	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ENABLE. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ENABLE = "DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ENABLE";

	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_DISABLE. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_DISABLE = "DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_DISABLE";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ERROR. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ERROR = "DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ERROR";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_NORMAL. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_NORMAL = "DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_NORMAL";

	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ENABLE. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ENABLE = "DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ENABLE";

	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_DISABLE. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_DISABLE = "DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_DISABLE";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_URL_SCHEME. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_URL_SCHEME = "DIRIGIBLE_SCHEDULER_EMAIL_URL_SCHEME";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_URL_HOST. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_URL_HOST = "DIRIGIBLE_SCHEDULER_EMAIL_URL_HOST";
	
	/** The Constant DIRIGIBLE_SCHEDULER_EMAIL_URL_PORT. */
	private static final String DIRIGIBLE_SCHEDULER_EMAIL_URL_PORT = "DIRIGIBLE_SCHEDULER_EMAIL_URL_PORT";
	
	/** The logs retantion in hours. */
	private static int logsRetantionInHours = 24*7;
	
	/** The email sender. */
	private static String emailSender = null;
	
	/** The email recipients line. */
	private static String emailRecipientsLine = null; 
	
	/** The email recipients. */
	private static String[] emailRecipients = null;
	
	/** The email subject error. */
	private static String emailSubjectError = null;
	
	/** The email subject normal. */
	private static String emailSubjectNormal = null;

	/** The email subject enable. */
	private static String emailSubjectEnable = null;

	/** The email subject disable. */
	private static String emailSubjectDisable = null;
	
	/** The email template error. */
	private static String emailTemplateError = null;
	
	/** The email template normal. */
	private static String emailTemplateNormal = null;

	/** The email template enable. */
	private static String emailTemplateEnable = null;

	/** The email template disable. */
	private static String emailTemplateDisable = null;
	
	/** The email url scheme. */
	private static String emailUrlScheme = null;
	
	/** The email url host. */
	private static String emailUrlHost = null;
	
	/** The email url port. */
	private static String emailUrlPort = null;
	
	/** The Constant DEFAULT_EMAIL_SUBJECT_ERROR. */
	private static final String DEFAULT_EMAIL_SUBJECT_ERROR = "Job execution failed: [%s]";
	
	/** The Constant DEFAULT_EMAIL_SUBJECT_NORMAL. */
	private static final String DEFAULT_EMAIL_SUBJECT_NORMAL = "Job execution is back to normal: [%s]";

	/** The Constant DEFAULT_EMAIL_SUBJECT_ENABLE. */
	private static final String DEFAULT_EMAIL_SUBJECT_ENABLE = "Job execution has been enabled: [%s]";

	/** The Constant DEFAULT_EMAIL_SUBJECT_DISABLE. */
	private static final String DEFAULT_EMAIL_SUBJECT_DISABLE = "Job execution has been disabled: [%s]";

	/** The Constant EMAIL_TEMPLATE_ERROR. */
	private static final String EMAIL_TEMPLATE_ERROR = "/job/templates/template-error.txt";

	/** The Constant EMAIL_TEMPLATE_NORMAL. */
	private static final String EMAIL_TEMPLATE_NORMAL = "/job/templates/template-normal.txt";

	/** The Constant EMAIL_TEMPLATE_ENABLE. */
	private static final String EMAIL_TEMPLATE_ENABLE = "/job/templates/template-enable.txt";

	/** The Constant EMAIL_TEMPLATE_DISABLE. */
	private static final String EMAIL_TEMPLATE_DISABLE = "/job/templates/template-disable.txt";
	
	static {
		try {
			logsRetantionInHours = Integer.parseInt(Configuration.get(DIRIGIBLE_SCHEDULER_LOGS_RETENTION_PERIOD, logsRetantionInHours + ""));
		} catch (Throwable e) {
			if (logger.isWarnEnabled()) {logger.warn(DIRIGIBLE_SCHEDULER_LOGS_RETENTION_PERIOD + " is not correctly set, so it will be backed up to a week timeframe (24x7)");}
			logsRetantionInHours = 24*7;
		}
		
		emailSender = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_SENDER);
		
		emailRecipientsLine = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_RECIPIENTS);
		if (emailRecipientsLine != null) {
			emailRecipients = emailRecipientsLine.split(",");
			for (String maybe : emailRecipients) {
				if (!EmailValidator.getInstance().isValid(maybe)) {
					emailRecipients = null;
					if (logger.isWarnEnabled()) {logger.warn(DIRIGIBLE_SCHEDULER_EMAIL_RECIPIENTS + " contains invalid e-mail address: " + maybe);}
					break;
				}
			}
		}
		
		emailSubjectError = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ERROR, DEFAULT_EMAIL_SUBJECT_ERROR);
		emailSubjectNormal = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_NORMAL, DEFAULT_EMAIL_SUBJECT_NORMAL);
		emailSubjectEnable = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_ENABLE, DEFAULT_EMAIL_SUBJECT_ENABLE);
		emailSubjectDisable = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_SUBJECT_DISABLE, DEFAULT_EMAIL_SUBJECT_DISABLE);
		emailTemplateError = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ERROR);
		emailTemplateNormal = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_NORMAL);
		emailTemplateEnable = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_ENABLE);
		emailTemplateDisable = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_TEMPLATE_DISABLE);
		emailUrlScheme = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_URL_SCHEME);
		emailUrlHost = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_URL_HOST);
		emailUrlPort = Configuration.get(DIRIGIBLE_SCHEDULER_EMAIL_URL_PORT);
	}
	

	// Jobs

	/**
	 * Creates the job.
	 *
	 * @param name the name
	 * @param group the group
	 * @param clazz the clazz
	 * @param handler the handler
	 * @param engine the engine
	 * @param description the description
	 * @param expression the expression
	 * @param singleton the singleton
	 * @param parameters the parameters
	 * @return the job definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Creates the or update job.
	 *
	 * @param jobDefinition the job definition
	 * @return the job definition
	 * @throws SchedulerException the scheduler exception
	 */
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
					if (existing.isEnabled() && !jobDefinition.isEnabled()) {
						String content = prepareEmail(jobDefinition, emailTemplateDisable, EMAIL_TEMPLATE_DISABLE);
						sendEmail(jobDefinition, emailSubjectDisable, content);
					} else if (!existing.isEnabled() && jobDefinition.isEnabled()) {
						String content = prepareEmail(jobDefinition, emailTemplateEnable, EMAIL_TEMPLATE_ENABLE);
						sendEmail(jobDefinition, emailSubjectEnable, content);
					}
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

	/**
	 * Creates the or update parameters.
	 *
	 * @param connection the connection
	 * @param jobDefinition the job definition
	 */
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

	/**
	 * Gets the job.
	 *
	 * @param name the name
	 * @return the job
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Removes the job.
	 *
	 * @param name the name
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Update job.
	 *
	 * @param name the name
	 * @param group the group
	 * @param clazz the clazz
	 * @param handler the handler
	 * @param engine the engine
	 * @param description the description
	 * @param expression the expression
	 * @param singleton the singleton
	 * @param parameters the parameters
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Gets the jobs.
	 *
	 * @return the jobs
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Exists job.
	 *
	 * @param name the name
	 * @return true, if successful
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#existsJob(java.lang.String)
	 */
	@Override
	public boolean existsJob(String name) throws SchedulerException {
		return getJob(name) != null;
	}

	/**
	 * Parses the job.
	 *
	 * @param json the json
	 * @return the job definition
	 */
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

	/**
	 * Parses the job.
	 *
	 * @param content the content
	 * @return the job definition
	 */
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

	/**
	 * Serialize job.
	 *
	 * @param jobDefinition the job definition
	 * @return the string
	 */
	@Override
	public String serializeJob(JobDefinition jobDefinition) {
		return GsonHelper.GSON.toJson(jobDefinition);
	}
	
	// Job Log
	

	/**
	 * Job triggered.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
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

	/**
	 * Job finished.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param triggeredId the triggered id
	 * @param triggeredAt the triggered at
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
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
		jobLogDefinition = registerJobLog(jobLogDefinition);
		JobDefinition jobDefinition = getJob(name);
		boolean statusChanged = jobDefinition.getStatus() != JobLogDefinition.JOB_LOG_STATUS_FINISHED;
		jobDefinition.setStatus(JobLogDefinition.JOB_LOG_STATUS_FINISHED);
		jobDefinition.setMessage("");
		jobDefinition.setExecutedAt(jobLogDefinition.getFinishedAt());
		createOrUpdateJob(jobDefinition);
		if (statusChanged) {
			String content = prepareEmail(jobDefinition, emailTemplateNormal, EMAIL_TEMPLATE_NORMAL);
			sendEmail(jobDefinition, emailSubjectNormal, content);
		}
		return jobLogDefinition;
	}
	
	/**
	 * Job failed.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param triggeredId the triggered id
	 * @param triggeredAt the triggered at
	 * @param message the message
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
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
		jobLogDefinition = registerJobLog(jobLogDefinition);
		JobDefinition jobDefinition = getJob(name);
		boolean statusChanged = jobDefinition.getStatus() != JobLogDefinition.JOB_LOG_STATUS_FAILED;
		jobDefinition.setStatus(JobLogDefinition.JOB_LOG_STATUS_FAILED);
		jobDefinition.setMessage(message);
		jobDefinition.setExecutedAt(jobLogDefinition.getFinishedAt());
		createOrUpdateJob(jobDefinition);
		if (statusChanged) {
			String content = prepareEmail(jobDefinition, emailTemplateError, EMAIL_TEMPLATE_ERROR);
			sendEmail(jobDefinition, emailSubjectError, content);
		}
		return jobLogDefinition;
	}
	
	/**
	 * Job logged.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param message the message
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobLogged(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobLogged(String name, String handler, String message) throws SchedulerException {
		return jobLogged(name, handler, message, JobLogDefinition.JOB_LOG_STATUS_LOGGED);
	}
	
	/**
	 * Job logged error.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param message the message
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobLoggedError(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobLoggedError(String name, String handler, String message) throws SchedulerException {
		return jobLogged(name, handler, message, JobLogDefinition.JOB_LOG_STATUS_ERROR);
	}
	
	/**
	 * Job logged warning.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param message the message
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobLoggedWarning(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobLoggedWarning(String name, String handler, String message) throws SchedulerException {
		return jobLogged(name, handler, message, JobLogDefinition.JOB_LOG_STATUS_WARN);
	}
	
	/**
	 * Job logged info.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param message the message
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#jobLoggedInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public JobLogDefinition jobLoggedInfo(String name, String handler, String message) throws SchedulerException {
		return jobLogged(name, handler, message, JobLogDefinition.JOB_LOG_STATUS_INFO);
	}
	
	/**
	 * Job logged.
	 *
	 * @param name the name
	 * @param handler the handler
	 * @param message the message
	 * @param severity the severity
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
	private JobLogDefinition jobLogged(String name, String handler, String message, short severity) throws SchedulerException {
		JobLogDefinition jobLogDefinition = new JobLogDefinition();
		jobLogDefinition.setName(name);
		jobLogDefinition.setHandler(handler);
		jobLogDefinition.setMessage(message);
		jobLogDefinition.setStatus(severity);
		jobLogDefinition.setTriggeredAt(new Timestamp(new java.util.Date().getTime()));
		return registerJobLog(jobLogDefinition);
	}
	
	/**
	 * Register job log.
	 *
	 * @param jobLogDefinition the job log definition
	 * @return the job log definition
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Gets the job logs.
	 *
	 * @param name the name
	 * @return the job logs
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobLogs(java.lang.String)
	 */
	@Override
	public List<JobLogDefinition> getJobLogs(String name) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				
				String sql = SqlFactory.getNative(connection).select().limit(1000).column("*").from("DIRIGIBLE_JOB_LOGS")
						.where("JOBLOG_NAME = ?").order("JOBLOG_TRIGGERED_AT", false).toString();
				return jobLogPersistenceManager.query(connection, JobLogDefinition.class, sql, Arrays.asList(name));
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/**
	 * Clear job logs.
	 *
	 * @param name the name
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#clearJobLogs()
	 */
	@Override
	public void clearJobLogs(String name) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_JOB_LOGS")
						.where("JOBLOG_NAME = ?")
						.build();
				jobLogPersistenceManager.tableCheck(connection, JobLogDefinition.class);
				jobLogPersistenceManager.execute(connection, sql, Arrays.asList(name));
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}
	
	/**
	 * Delete old job logs.
	 *
	 * @throws SchedulerException the scheduler exception
	 */
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
				jobLogPersistenceManager.execute(connection, sql, new Timestamp(System.currentTimeMillis() - logsRetantionInHours*60*60*1000)); // older than a week
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * Cleanup.
	 */
	@Override
	public void cleanup() {
		try {
			deleteOldJobLogs();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the job parameters.
	 *
	 * @param name the name
	 * @return the job parameters
	 * @throws SchedulerException the scheduler exception
	 */
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
	
	/**
	 * Prepare email.
	 *
	 * @param jobDefinition the job definition
	 * @param templateLocation the template location
	 * @param defaultLocation the default location
	 * @return the string
	 */
	private String prepareEmail(JobDefinition jobDefinition, String templateLocation, String defaultLocation) {
		RegistryResourceExecutor registryResourceExecutor = new RegistryResourceExecutor();
		byte[] template = registryResourceExecutor.getRegistryContent(templateLocation);
		if (template == null) {
			template = registryResourceExecutor.getRegistryContent(defaultLocation);
			if (template == null) {
				if (logger.isErrorEnabled()) {logger.error("Template for the e-mail has not been set nor the default one is available");}
				return null;
			}
		}
		IGenerationEngine generationEngine = GenerationEnginesManager.getGenerationEngine("mustache");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("job.name", jobDefinition.getName());
		parameters.put("job.message", jobDefinition.getMessage());
		parameters.put("job.scheme", emailUrlScheme);
		parameters.put("job.host", emailUrlHost);
		parameters.put("job.port", emailUrlPort);
		try {
			byte[] generated = generationEngine.generate(parameters, "~/temp", template);
			return new String(generated, StandardCharsets.UTF_8);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {logger.error("Error on generating the e-mail body: " + e.getMessage(),e);}
			return null;
		}
	}
	
	/**
	 * Send email.
	 *
	 * @param jobDefinition the job definition
	 * @param emailSubject the email subject
	 * @param emailContent the email content
	 */
	private void sendEmail(JobDefinition jobDefinition, String emailSubject, String emailContent) {
		try {
			
			List<JobEmailDefinition> emailDefinitions = getJobEmails(jobDefinition.getName());
			String[] emails = new String[emailDefinitions.size()];
			for (int i = 0; i < emails.length; i++) {
				emails[i] = emailDefinitions.get(i).getEmail();
			}
			
			if (emailSender != null
					&& (
						(emailRecipients != null && emailRecipients.length > 0) 
							|| emails.length > 0)
					) {
				
				List<Map> parts = new ArrayList<Map>();
				Map<String, String> map  = new HashedMap();
				map.put("contentType", ContentTypeHelper.TEXT_PLAIN);
				map.put("type", "text");
				map.put("text", emailContent);
				parts.add(map);
				MailFacade.getInstance().send(emailSender, emails.length > 0 ? emails : emailRecipients, null, null, 
						String.format(emailSubject, jobDefinition.getName()), parts);
	//		String from, String[] to, String[] cc, String[] bcc, String subject, List<Map> parts
			} else {
				if (emailRecipientsLine != null) {
					if (logger.isErrorEnabled()) {logger.error("DIRIGIBLE_SCHEDULER_EMAIL_* environment variables are not set correctly");}
				}
			}
		} catch (MessagingException | IOException | SchedulerException e) {
			if (logger.isErrorEnabled()) {logger.error("Sending an e-mail failed with: " + e.getMessage(), e);}
		}
		
	}

	/**
	 * Gets the job emails.
	 *
	 * @param name the name
	 * @return the job emails
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#getJobEmails(java.lang.String)
	 */
	@Override
	public List<JobEmailDefinition> getJobEmails(String name) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_JOB_EMAILS")
						.where("JOBEMAIL_JOB_NAME = ?").toString();
				return jobEmailPersistenceManager.query(connection, JobEmailDefinition.class, sql, Arrays.asList(name));
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * Adds the job email.
	 *
	 * @param name the name
	 * @param email the email
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#addJobEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public void addJobEmail(String name, String email) throws SchedulerException {
		
		if (!EmailValidator.getInstance().isValid(email)) {
			throw new SchedulerException("e-mail provided is not valid: " + email);
		}
		
		try {
			JobEmailDefinition jobEmailDefinition = new JobEmailDefinition();
			jobEmailDefinition.setJobName(name);
			jobEmailDefinition.setEmail(email);
			try (Connection connection = getDataSource().getConnection()) {
				jobEmailPersistenceManager.insert(connection, jobEmailDefinition);
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

	/**
	 * Removes the job email.
	 *
	 * @param id the id
	 * @throws SchedulerException the scheduler exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService#removeJobEmail(java.lang.Long)
	 */
	@Override
	public void removeJobEmail(Long id) throws SchedulerException {
		try {
			try (Connection connection = getDataSource().getConnection()) {
				jobEmailPersistenceManager.delete(connection, JobEmailDefinition.class, id);
			}
		} catch (SQLException e) {
			throw new SchedulerException(e);
		}
	}

}
