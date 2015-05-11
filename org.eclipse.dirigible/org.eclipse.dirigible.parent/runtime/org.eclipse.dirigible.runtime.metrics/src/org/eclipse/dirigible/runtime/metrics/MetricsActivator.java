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

package org.eclipse.dirigible.runtime.metrics;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.eclipse.dirigible.runtime.memory.MemoryLogCleanupTask;
import org.eclipse.dirigible.runtime.memory.MemoryLogTask;
import org.eclipse.dirigible.runtime.task.TaskManagerLong;
import org.eclipse.dirigible.runtime.task.TaskManagerMedium;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

public class MetricsActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		
//		SchedulerActivator.getSchedulerServlet().startSchedulers();
		
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
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		

	}

}
