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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.JobParameterDefinition;
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
		Collection<JobParameterDefinition> parameters = new ArrayList<JobParameterDefinition>();
		JobParameterDefinition parameter = new JobParameterDefinition();
		parameter.setId(jobName, "param1");
		parameter.setJobName(jobName);
		parameter.setName("param1");
		parameter.setType("string");
		parameter.setDefaultValue("default_value");
		parameters.add(parameter);
		jobCoreService.createJob(jobName, "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false, parameters);
		JobDefinition jobDefinition = jobCoreService.getJob(jobName);

		assertEquals(jobName, jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		JobParameterDefinition param = jobDefinition.getParameters().toArray(new JobParameterDefinition[] {})[0];
		assertEquals(jobName, param.getJobName());
		assertEquals("string", param.getType());
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
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false, Collections.EMPTY_LIST);
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
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false, Collections.EMPTY_LIST);
		JobDefinition jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.updateJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test 2", "expr...", false, Collections.EMPTY_LIST);
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
		jobCoreService.createJob("test_job1", "test_group", "org....", "handler.js", "engine type", "Test", "expr...", false, Collections.EMPTY_LIST);
		JobDefinition jobDefinition = jobCoreService.getJob("test_job1");
		assertEquals("test_job1", jobDefinition.getName());
		assertEquals("Test", jobDefinition.getDescription());
		jobCoreService.removeJob("test_job1");
		jobDefinition = jobCoreService.getJob("test_job1");
		assertNull(jobDefinition);
	}
	
	/**
	 * Serialize the job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void serializeJob() throws SchedulerException {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("test_job");
		jobDefinition.setGroup("test_group");
		jobDefinition.setHandler("test_handler.js");
		jobDefinition.setEngine("js");
		jobDefinition.setDescription("description ...");
		jobDefinition.setExpression("0/1 * * * * ?");
		jobDefinition.addParameter("param1", "string", "", "", "");
		jobDefinition.addParameter("param2", "number", "0", "", "");
		String content = jobCoreService.serializeJob(jobDefinition);
		int index = content.indexOf("id");
		assertEquals(-1, index);
		index = content.indexOf("param1");
		assertTrue(index > 0);
		index = content.indexOf("number");
		assertTrue(index > 0);
	}
	
	/**
	 * Serialize the job.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void parseJob() throws SchedulerException {
		String content = "{\"name\":\"test_job\",\"group\":\"test_group\",\"clazz\":\"\",\"description\":\"description ...\",\"expression\":\"0/1 * * * * ?\",\"handler\":\"test_handler.js\",\"engine\":\"js\",\"singleton\":false,\"enabled\":true,\"parameters\":[{\"name\":\"param1\",\"type\":\"string\",\"defaultValue\":\"\",\"choices\":\"\"},{\"name\":\"param2\",\"type\":\"number\",\"defaultValue\":\"0\",\"choices\":\"\"}]}";
		JobDefinition jobDefinition = jobCoreService.parseJob(content);
		JobParameterDefinition parameter = jobDefinition.getParameters().toArray(new JobParameterDefinition[] {})[0];
		assertEquals("\"test_job\":\"param1\"", parameter.getId());
		assertEquals("test_job", parameter.getJobName());
	}

}
