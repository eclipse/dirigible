/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.job.JobsSynchronizer;
import org.eclipse.dirigible.runtime.messaging.MessagingSynchronizer;
import org.eclipse.dirigible.runtime.repository.RepositoryHistoryCleanupTask;
import org.eclipse.dirigible.runtime.search.RebuildSearchIndexTask;
import org.eclipse.dirigible.runtime.search.UpdateSearchIndexTask;
import org.eclipse.dirigible.runtime.security.SecuritySynchronizer;
import org.eclipse.dirigible.runtime.task.TaskManagerLong;
import org.eclipse.dirigible.runtime.task.TaskManagerMedium;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

public class SchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = -3775928162856885854L;

	private static final Logger logger = Logger.getLogger(SchedulerServlet.class);

	private static ScheduledExecutorService securitySynchronizerScheduler;
	private static ScheduledExecutorService jobsSynchronizerScheduler;
	private static ScheduledExecutorService messagingSynchronizerScheduler;
	private static ScheduledExecutorService taskManagerShortScheduler;
	private static ScheduledExecutorService taskManagerMediumScheduler;
	private static ScheduledExecutorService taskManagerLongScheduler;

	private static final Object LOCK = new Object();
	private static volatile boolean started = false;

	@Override
	public void init() throws ServletException {
		super.init();
		startSchedulers();
	}

	public void startSchedulers() {
		if (!started) {
			synchronized (LOCK) {
				if (!started) {
					logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
							+ "contextInitialized"); //$NON-NLS-1$

					securitySynchronizerScheduler = Executors.newSingleThreadScheduledExecutor();
					securitySynchronizerScheduler.scheduleAtFixedRate(new SecuritySynchronizer(), 1, 1, TimeUnit.MINUTES);

					jobsSynchronizerScheduler = Executors.newSingleThreadScheduledExecutor();
					jobsSynchronizerScheduler.scheduleAtFixedRate(new JobsSynchronizer(), 1, 1, TimeUnit.MINUTES);
					
					messagingSynchronizerScheduler = Executors.newSingleThreadScheduledExecutor();
					messagingSynchronizerScheduler.scheduleAtFixedRate(new MessagingSynchronizer(), 1, 1, TimeUnit.MINUTES);

					taskManagerShortScheduler = Executors.newSingleThreadScheduledExecutor();
					taskManagerShortScheduler.scheduleAtFixedRate(TaskManagerShort.getInstance(), 10, 10, TimeUnit.SECONDS);

					taskManagerMediumScheduler = Executors.newSingleThreadScheduledExecutor();
					taskManagerMediumScheduler.scheduleAtFixedRate(TaskManagerMedium.getInstance(), 1, 1, TimeUnit.MINUTES);

					taskManagerLongScheduler = Executors.newSingleThreadScheduledExecutor();
					taskManagerLongScheduler.scheduleAtFixedRate(TaskManagerLong.getInstance(), 1, 1, TimeUnit.HOURS);

					registerRunnableTasks();
					started = true;

					logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
							+ "contextInitialized"); //$NON-NLS-1$
				}
			}
		}
	}

	@Override
	public void destroy() {

		stopSchedulers();

		super.destroy();
	}

	void stopSchedulers() {
		if (started) {
			synchronized (LOCK) {
				if (started) {
					logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
							+ "contextDestroyed"); //$NON-NLS-1$
					securitySynchronizerScheduler.shutdownNow();
					jobsSynchronizerScheduler.shutdownNow();
					messagingSynchronizerScheduler.shutdownNow();
					taskManagerShortScheduler.shutdownNow();
					taskManagerMediumScheduler.shutdownNow();
					taskManagerLongScheduler.shutdownNow();
					started = false;

					logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
							+ "contextDestroyed"); //$NON-NLS-1$
				}
			}
		}
	}

	private void registerRunnableTasks() {
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "registerRunnableTasks"); //$NON-NLS-1$

//		// short
//		AccessLogLocationsSynchronizer accessLogLocationsSynchronizer = new AccessLogLocationsSynchronizer();
//		TaskManagerShort.getInstance().registerRunnableTask(accessLogLocationsSynchronizer);
//
//		// medium
//		MemoryLogTask memoryLogTask = new MemoryLogTask();
//		TaskManagerMedium.getInstance().registerRunnableTask(memoryLogTask);

		UpdateSearchIndexTask updateSearchIndexTask = new UpdateSearchIndexTask();
		TaskManagerLong.getInstance().registerRunnableTask(updateSearchIndexTask);

//		// long
//		AccessLogCleanupTask accessLogCleanupTask = new AccessLogCleanupTask();
//		TaskManagerLong.getInstance().registerRunnableTask(accessLogCleanupTask);

		RepositoryHistoryCleanupTask historyCleanupTask = new RepositoryHistoryCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(historyCleanupTask);

//		MemoryLogCleanupTask memoryLogCleanupTask = new MemoryLogCleanupTask();
//		TaskManagerLong.getInstance().registerRunnableTask(memoryLogCleanupTask);

		RebuildSearchIndexTask rebuildSearchIndexTask = new RebuildSearchIndexTask();
		TaskManagerLong.getInstance().registerRunnableTask(rebuildSearchIndexTask);

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "registerRunnableTasks"); //$NON-NLS-1$
	}

	public ScheduledExecutorService getSecuritySynchronizerScheduler() {
		return securitySynchronizerScheduler;
	}

	public ScheduledExecutorService getJobsSynchronizerScheduler() {
		return jobsSynchronizerScheduler;
	}
	
	public ScheduledExecutorService getMessagingSynchronizerScheduler() {
		return messagingSynchronizerScheduler;
	}

	public ScheduledExecutorService getTaskManagerShortScheduler() {
		return taskManagerShortScheduler;
	}

	public ScheduledExecutorService getTaskManagerMediumScheduler() {
		return taskManagerMediumScheduler;
	}

	public ScheduledExecutorService getTaskManagerLongScheduler() {
		return taskManagerLongScheduler;
	}

}
