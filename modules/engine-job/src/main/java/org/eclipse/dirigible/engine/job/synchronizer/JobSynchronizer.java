/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.job.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.eclipse.dirigible.repository.api.IResource;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JobSynchronizer.
 */
@Singleton
public class JobSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(JobSynchronizer.class);

	private static final Map<String, JobDefinition> JOBS_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, JobDefinition>());

	private static final List<String> JOBS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	@Inject
	private SchedulerCoreService schedulerCoreService;

	// @Inject
	// private SchedulerManager schedulerManager;

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		JobSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(JobSynchronizer.class);
		extensionsSynchronizer.synchronize();
	}

	/**
	 * Register predelivered job.
	 *
	 * @param jobPath
	 *            the job path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredJob(String jobPath) throws IOException {
		InputStream in = JobSynchronizer.class.getResourceAsStream(jobPath);
		String json = IOUtils.toString(in, StandardCharsets.UTF_8);
		JobDefinition jobDefinition = schedulerCoreService.parseJob(json);
		jobDefinition.setName(jobPath);
		JOBS_PREDELIVERED.put(jobPath, jobDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (JobSynchronizer.class) {
			logger.trace("Synchronizing Jobs...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				startJobs();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Jobs failed.", e);
			}
			logger.trace("Done synchronizing Jobs.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Jobs from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Jobs from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(ISchedulerCoreService.FILE_EXTENSION_JOB)) {
			JobDefinition jobDefinition = schedulerCoreService.parseJob(resource.getContent());
			jobDefinition.setName(getRegistryPath(resource));
			synchronizeJob(jobDefinition);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Jobs...");

		try {
			List<JobDefinition> jobDefinitions = schedulerCoreService.getJobs();
			for (JobDefinition jobDefinition : jobDefinitions) {
				if (!JOBS_SYNCHRONIZED.contains(jobDefinition.getName())) {
					schedulerCoreService.removeJob(jobDefinition.getName());
					logger.warn("Cleaned up Job [{}] from group: {}", jobDefinition.getName(), jobDefinition.getGroup());
				}
			}
		} catch (SchedulerException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Jobs.");
	}

	private void startJobs() throws SchedulerException {
		logger.trace("Start Jobs...");

		for (String jobName : JOBS_SYNCHRONIZED) {
			if (!SchedulerManager.existsJob(jobName)) {
				try {
					JobDefinition jobDefinition = schedulerCoreService.getJob(jobName);
					SchedulerManager.scheduleJob(jobDefinition);
				} catch (SchedulerException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		Set<TriggerKey> runningJobs = SchedulerManager.listJobs();
		for (TriggerKey jobKey : runningJobs) {
			try {
				if (!JOBS_SYNCHRONIZED.contains(jobKey.getName())) {
					SchedulerManager.unscheduleJob(jobKey.getName(), jobKey.getGroup());
				}
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.trace("Running Jobs: " + runningJobs.size());
		logger.trace("Done starting Jobs.");
	}

	private void clearCache() {
		JOBS_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Jobs...");
		// Jobs
		for (JobDefinition jobDefinition : JOBS_PREDELIVERED.values()) {
			synchronizeJob(jobDefinition);
		}
		logger.trace("Done synchronizing predelivered Jobs.");
	}

	private void synchronizeJob(JobDefinition jobDefinition) throws SynchronizationException {
		try {
			if (!schedulerCoreService.existsJob(jobDefinition.getName())) {
				schedulerCoreService.createJob(jobDefinition.getName(), jobDefinition.getGroup(), jobDefinition.getClazz(),
						jobDefinition.getHandler(), jobDefinition.getEngine(), jobDefinition.getDescription(), jobDefinition.getExpression(),
						jobDefinition.isSingleton());
				logger.info("Synchronized a new Job [{}] from group: {}", jobDefinition.getName(), jobDefinition.getGroup());
			} else {
				JobDefinition existing = schedulerCoreService.getJob(jobDefinition.getName());
				if (!jobDefinition.equals(existing)) {
					schedulerCoreService.updateJob(jobDefinition.getName(), jobDefinition.getGroup(), jobDefinition.getClazz(),
							jobDefinition.getHandler(), jobDefinition.getEngine(), jobDefinition.getDescription(), jobDefinition.getExpression(),
							jobDefinition.isSingleton());
					logger.info("Synchronized a modified Job [{}] from group: {}", jobDefinition.getName(), jobDefinition.getGroup());
				}
			}
			JOBS_SYNCHRONIZED.add(jobDefinition.getName());
		} catch (SchedulerException e) {
			throw new SynchronizationException(e);
		}
	}

}
