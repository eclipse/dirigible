/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow;

import java.io.InputStream;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.flow.log.FlowLogCleanupTask;
import org.eclipse.dirigible.runtime.job.log.JobLogCleanupTask;
import org.eclipse.dirigible.runtime.listener.log.ListenerLogCleanupTask;
import org.eclipse.dirigible.runtime.listener.message.MessageListenerTask;
import org.eclipse.dirigible.runtime.task.TaskManagerLong;
import org.eclipse.dirigible.runtime.task.TaskManagerMedium;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class FlowsActivator implements BundleActivator {
	private static final Logger logger = Logger.getLogger(FlowsActivator.class);
	private static final String SCHEDULER_NAME = "org.eclipse.dirigible.runtime.flow-QuartzScheduler";
	private static BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		FlowsActivator.context = context;

		// ListenerEventProcessorFactory.registerListenerEventProcessorProviders(context);

		FlowLogCleanupTask flowLogCleanupTask = new FlowLogCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(flowLogCleanupTask);

		JobLogCleanupTask jobLogCleanupTask = new JobLogCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(jobLogCleanupTask);

		ListenerLogCleanupTask listenerLogCleanupTask = new ListenerLogCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(listenerLogCleanupTask);

		MessageListenerTask messageListenerTask = new MessageListenerTask();
		TaskManagerMedium.getInstance().registerRunnableTask(messageListenerTask);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		InputStream schedulerConfig = FlowsActivator.class.getResourceAsStream("/scheduler.properties");
		try {
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
			schedulerFactory.initialize(schedulerConfig);
			Scheduler scheduler = schedulerFactory.getScheduler(SCHEDULER_NAME);
			if (null != scheduler) {
				scheduler.shutdown();
			}
		} finally {
			if (null != schedulerConfig) {
				try {
					schedulerConfig.close();
				} catch (Exception exception) {
					if (logger.isErrorEnabled()) {
						logger.error("Cannot close the stream to the scheduler configuration.", exception);
					}
				}
			}
		}
	}

	public static BundleContext getContext() {
		return context;
	}

}
