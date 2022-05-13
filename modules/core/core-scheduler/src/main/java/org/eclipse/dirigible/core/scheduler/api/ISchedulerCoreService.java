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
package org.eclipse.dirigible.core.scheduler.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Scheduler Core Service interface.
 */
public interface ISchedulerCoreService extends ICoreService {

	/** The job file extension */
	public String FILE_EXTENSION_JOB = ".job";

	/** The internal jobs */
	public String JOB_GROUP_INTERNAL = "dirigible-internal";

	/** The user defined jobs */
	public String JOB_GROUP_DEFINED = "dirigible-defined";

	/** The handler parameter */
	public String JOB_PARAMETER_HANDLER = "dirigible-job-handler";

	/** The engine type */
	public String JOB_PARAMETER_ENGINE = "dirigible-engine-type";

	/**
	 * Creates the job with parameters.
	 *
	 * @param name
	 *            the name
	 * @param group
	 *            the group
	 * @param clazz
	 *            the job class
	 * @param handler
	 *            the handler
	 * @param engine
	 *            the engine type
	 * @param description
	 *            the description
	 * @param expression
	 *            the expression
	 * @param singleton
	 *            the singleton
	 * @return the job definition
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public JobDefinition createJob(String name, String group, String clazz, String handler, String engine, String description, String expression,
			boolean singleton) throws SchedulerException;

	/**
	 * Creates the job by definition.
	 *
	 * @param jobDefinition
	 *            the job definition
	 * @return the job definition
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public JobDefinition createOrUpdateJob(JobDefinition jobDefinition) throws SchedulerException;

	/**
	 * Gets the job.
	 *
	 * @param name
	 *            the name
	 * @return the job
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public JobDefinition getJob(String name) throws SchedulerException;

	/**
	 * Removes the job.
	 *
	 * @param name
	 *            the name
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public void removeJob(String name) throws SchedulerException;

	/**
	 * Update job.
	 *
	 * @param name
	 *            the name
	 * @param group
	 *            the group
	 * @param clazz
	 *            the job class
	 * @param handler
	 *            the handler
	 * @param engine
	 *            the engine type
	 * @param description
	 *            the description
	 * @param expression
	 *            the expression
	 * @param singleton
	 *            the singleton
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public void updateJob(String name, String group, String clazz, String handler, String engine, String description, String expression,
			boolean singleton) throws SchedulerException;

	/**
	 * Gets the jobs.
	 *
	 * @return the jobs
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public List<JobDefinition> getJobs() throws SchedulerException;

	/**
	 * Checks whether a job with the given name already exist
	 *
	 * @param name
	 *            the name
	 * @return true if exists and false otherwise
	 * @throws SchedulerException
	 *             in case of an internal error
	 */
	public boolean existsJob(String name) throws SchedulerException;

	/**
	 * Parses the job.
	 *
	 * @param json
	 *            the json
	 * @return the job definition
	 */
	public JobDefinition parseJob(String json);

	/**
	 * Parses the job.
	 *
	 * @param content
	 *            the content
	 * @return the job definition
	 */
	public JobDefinition parseJob(byte[] content);

	/**
	 * Serializes the job definition
	 *
	 * @param jobDefinition
	 *            the job definition
	 * @return serialized definition as string
	 */
	public String serializeJob(JobDefinition jobDefinition);

}
