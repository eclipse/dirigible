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
package org.eclipse.dirigible.core.scheduler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class JobCoreServiceTest.
 */
public class JobCoreServiceTest extends AbstractDirigibleTest {

	/** The job core service. */
	private ISchedulerCoreService jobCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.jobCoreService = new SchedulerCoreService();
	}

	/**
	 * Creates the job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void createJob() throws SchedulerException {
		String jobName = "test_job1";
		jobCoreService.removeJob(jobName);
		jobCoreService.createJob(jobName, "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false);
		JobDefinition jobDefinition = jobCoreService.getJob(jobName);

		assertEquals(jobName, jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.removeJob(jobName);
	}

	/**
	 * Gets the job.
	 *
	 * @return the job
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void getJob() throws SchedulerException {
		jobCoreService.removeJob("test_job1");
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false);
		JobDefinition jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
	}

	/**
	 * Updatet job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void updatetJob() throws SchedulerException {
		jobCoreService.removeJob("test_job1");
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false);
		JobDefinition jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.updateJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test 2", "expr...", false);
		jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test 2", jobDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
	}

	/**
	 * Removes the job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void removeJob() throws SchedulerException {
		jobCoreService.removeJob("test_job1");
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false);
		JobDefinition jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
		jobDefinition = jobCoreService.getJob("test_job1");
		assertNull(jobDefinition);
	}

}
