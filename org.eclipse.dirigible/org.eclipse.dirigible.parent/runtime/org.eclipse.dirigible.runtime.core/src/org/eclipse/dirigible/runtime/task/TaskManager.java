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

package org.eclipse.dirigible.runtime.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.eclipse.dirigible.repository.logging.Logger;

public abstract class TaskManager implements Runnable {

	private static final Logger logger = Logger.getLogger(TaskManager.class);

	private List<IRunnableTask> runnableTasks = Collections
			.synchronizedList(new ArrayList<IRunnableTask>());
	
	private List<IRunnableTask> forRemove = Collections
			.synchronizedList(new ArrayList<IRunnableTask>());
	
	@Override
	public void run() {

		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$

		try {
			startRunnableTasks();
			logger.debug("All tasks were performed successfylly: " //$NON-NLS-1$
					+ runnableTasks.size());
		} catch (Exception e) {
			logger.error("Task Manager error", e);
		}

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "run"); //$NON-NLS-1$
	}

	// public List<IRunnableTask> getRunnableTasks() {
	// return runnableTasks;
	// }

	public void registerRunnableTask(IRunnableTask runnableTask) {
	
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "registerRunnableTask"); //$NON-NLS-1$

		runnableTasks.add(runnableTask);
		logger.debug("registered runnable task: " + runnableTask.getName()); //$NON-NLS-1$

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "registerRunnableTask"); //$NON-NLS-1$

	}

	public void unregisterRunnableTask(IRunnableTask runnableTask) {
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "unregisterRunnableTask"); //$NON-NLS-1$

		forRemove.add(runnableTask);

		logger.debug("unregistered runnable task: " + runnableTask.getName()); //$NON-NLS-1$

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "unregisterRunnableTask"); //$NON-NLS-1$
	}
	
	private void removeRunnableTask(IRunnableTask runnableTask) {
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "removeRunnableTask"); //$NON-NLS-1$

		runnableTasks.remove(runnableTask);
		logger.debug("unregistered runnable task: " + runnableTask.getName()); //$NON-NLS-1$

		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "removeRunnableTask"); //$NON-NLS-1$
	}

	private void startRunnableTasks() throws ServletException {
		
		// remove the tasks registered for removing first
		for (IRunnableTask runnableTask : forRemove) {
			removeRunnableTask(runnableTask);
		}
		
		// start consecutively all available tasks
		for (Iterator<IRunnableTask> iterator = runnableTasks.iterator(); iterator.hasNext();) {
			IRunnableTask task = iterator.next();
			try {
				logger.debug("Staring Task: " + task.getName() + "..."); //$NON-NLS-1$ //$NON-NLS-2$
				task.start();
				logger.debug("Task: " + task.getName() + " - " //$NON-NLS-1$ //$NON-NLS-2$
						+ "ended."); //$NON-NLS-1$
			} catch (Exception e) {
				logger.error("Task Manager error for Task: " + task.getName(), e);
			}
		}
	}
}
