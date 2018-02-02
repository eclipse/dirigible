/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.job.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.engine.job.synchronizer.JobSynchronizer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class JobSynchronizerTest.
 */
public class JobSynchronizerTest extends AbstractGuiceTest {

	@Inject
	private SchedulerInitializer schedulerInitializer;

	@Inject
	private ISchedulerCoreService schedulerCoreService;

	@Inject
	private JobSynchronizer jobSynchronizer;

	@Inject
	private SchedulerManager schedulerManager;

	@Inject
	private IRepository repository;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.schedulerInitializer = getInjector().getInstance(SchedulerInitializer.class);
		this.schedulerCoreService = getInjector().getInstance(SchedulerCoreService.class);
		this.jobSynchronizer = getInjector().getInstance(JobSynchronizer.class);
		this.schedulerManager = getInjector().getInstance(SchedulerManager.class);
		this.repository = getInjector().getInstance(IRepository.class);

		schedulerInitializer.initialize();
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		SchedulerManager.shutdownScheduler();
	}

	/**
	 * Full job test.
	 *
	 * @throws SchedulerException
	 *             the job exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void fullJobTest() throws SchedulerException, IOException, InterruptedException {
		createJobTest();

		Thread.sleep(5000);

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.job");

		jobSynchronizer.synchronize();

		JobDefinition jobDefinition = schedulerCoreService.getJob("/custom/custom.job");
		assertNull(jobDefinition);
		assertFalse(schedulerManager.existsJob("/custom/custom.job"));

		Thread.sleep(5000);
	}

	/**
	 * Creates the job test.
	 *
	 * @throws JobException
	 *             the job exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createJobTest() throws SchedulerException, IOException {
		jobSynchronizer.registerPredeliveredJob("/control/control.job");

		JobDefinition jobDefinitionCustom = new JobDefinition();
		jobDefinitionCustom.setName("/custom/custom.job");
		jobDefinitionCustom.setHandler("custom/custom");
		jobDefinitionCustom.setExpression("0/1 * * * * ?");
		jobDefinitionCustom.setDescription("Test");
		jobDefinitionCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		jobDefinitionCustom.setCreatedBy("test_user");

		String json = schedulerCoreService.serializeJob(jobDefinitionCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.job", json.getBytes());

		jobSynchronizer.synchronize();

		JobDefinition jobDefinition = schedulerCoreService.getJob("/control/control.job");
		assertNotNull(jobDefinition);
		jobDefinition = schedulerCoreService.getJob("/custom/custom.job");
		assertNotNull(jobDefinition);

	}

}
