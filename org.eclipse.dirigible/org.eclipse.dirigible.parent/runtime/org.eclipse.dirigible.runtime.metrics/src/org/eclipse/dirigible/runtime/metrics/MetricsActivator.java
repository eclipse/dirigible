/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.metrics;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.log.WebSocketLogBridgeServletInternal;
import org.eclipse.dirigible.runtime.memory.MemoryLogCleanupTask;
import org.eclipse.dirigible.runtime.memory.MemoryLogTask;
import org.eclipse.dirigible.runtime.task.TaskManagerLong;
import org.eclipse.dirigible.runtime.task.TaskManagerMedium;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MetricsActivator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(MetricsActivator.class);

	WebSocketLogBridgeServletInternal webSocketLogBridgeServletInternal;

	@Override
	public void start(BundleContext context) throws Exception {

		// SchedulerActivator.getSchedulerServlet().startSchedulers();

		// short
		AccessLogLocationsSynchronizer accessLogLocationsSynchronizer = new AccessLogLocationsSynchronizer();
		TaskManagerShort.getInstance().registerRunnableTask(accessLogLocationsSynchronizer);

		// medium
		MemoryLogTask memoryLogTask = new MemoryLogTask();
		TaskManagerMedium.getInstance().registerRunnableTask(memoryLogTask);

		// long
		AccessLogCleanupTask accessLogCleanupTask = new AccessLogCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(accessLogCleanupTask);

		MemoryLogCleanupTask memoryLogCleanupTask = new MemoryLogCleanupTask();
		TaskManagerLong.getInstance().registerRunnableTask(memoryLogCleanupTask);

		setupLogChannel();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		webSocketLogBridgeServletInternal.closeAll();
		Logger.removeListener(webSocketLogBridgeServletInternal);
	}

	protected void setupLogChannel() {

		logger.debug("Setting log channel internal ...");

		webSocketLogBridgeServletInternal = new WebSocketLogBridgeServletInternal();
		System.getProperties().put("websocket_log_channel_internal", webSocketLogBridgeServletInternal);

		Logger.addListener(webSocketLogBridgeServletInternal);

		logger.debug("Log channel internal has been set.");

	}

}
