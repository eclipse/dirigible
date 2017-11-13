/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Scheduler Core Service interface.
 */
public interface ISchedulerCoreService extends ICoreService {

	/**
	 * Creates the job with parameters.
	 *
	 * @param name
	 *            the name
	 * @param group
	 *            the group
	 * @param clazz
	 *            the clazz
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
	public JobDefinition createJob(String name, String group, String clazz, String description, String expression, boolean singleton)
			throws SchedulerException;

	/**
	 * Creates the job by definition.
	 *
	 * @param jobDefinition
	 *            the job definition
	 * @return the job definition
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public JobDefinition createJob(JobDefinition jobDefinition) throws SchedulerException;

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
	 *            the clazz
	 * @param description
	 *            the description
	 * @param expression
	 *            the expression
	 * @param singleton
	 *            the singleton
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public void updateJob(String name, String group, String clazz, String description, String expression, boolean singleton)
			throws SchedulerException;

	/**
	 * Gets the jobs.
	 *
	 * @return the jobs
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public List<JobDefinition> getJobs() throws SchedulerException;

}
