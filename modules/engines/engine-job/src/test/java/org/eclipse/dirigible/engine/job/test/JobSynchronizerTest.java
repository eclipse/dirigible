/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.job.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.job.synchronizer.JobSynchronizer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class JobSynchronizerTest.
 */
public class JobSynchronizerTest extends AbstractDirigibleTest {

	/** The scheduler initializer. */
	private SchedulerInitializer schedulerInitializer;

	/** The scheduler core service. */
	private ISchedulerCoreService schedulerCoreService;

	/** The job synchronizer. */
	private JobSynchronizer jobSynchronizer;

	/** The scheduler manager. */
	private SchedulerManager schedulerManager;

	/** The repository. */
	private IRepository repository;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.schedulerInitializer = new SchedulerInitializer();
		this.schedulerCoreService = new SchedulerCoreService();
		this.jobSynchronizer = new JobSynchronizer();
		this.schedulerManager = new SchedulerManager();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

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
	 * @throws SchedulerException the scheduler exception
	 * @throws IOException             Signals that an I/O exception has occurred.
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
