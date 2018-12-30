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
package org.eclipse.dirigible.core.scheduler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class JobCoreServiceTest.
 */
public class JobCoreServiceTest extends AbstractGuiceTest {

	/** The job core service. */
	@Inject
	private ISchedulerCoreService jobCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.jobCoreService = getInjector().getInstance(SchedulerCoreService.class);
	}

	/**
	 * Creates the job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void createJob() throws SchedulerException {
		jobCoreService.removeJob("test_job1");
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false);
		List<JobDefinition> list = jobCoreService.getJobs();
		assertEquals(1, list.size());
		JobDefinition jobDefinition = list.get(0);
		System.out.println(jobDefinition.toString());
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
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
		JobDefinition extensionPointDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
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
		JobDefinition extensionPointDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		jobCoreService.updateJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test 2", "expr...", false);
		extensionPointDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", extensionPointDefinition.getName());
		assertEquals("Test 2", extensionPointDefinition.getDescription());
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
		JobDefinition extensionPointDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", extensionPointDefinition.getName());
		assertEquals("Test", extensionPointDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
		extensionPointDefinition = jobCoreService.getJob("test_job1");
		assertNull(extensionPointDefinition);
	}

}
