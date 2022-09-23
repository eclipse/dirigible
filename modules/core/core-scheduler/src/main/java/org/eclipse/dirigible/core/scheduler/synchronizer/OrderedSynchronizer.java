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
package org.eclipse.dirigible.core.scheduler.synchronizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.IOrderedSynchronizerContribution;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OrderedSynchronizer Synchronizer.
 */
public class OrderedSynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OrderedSynchronizer.class);
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();
	
	/** The Constant CONTRIBUTIONS. */
	private static final ServiceLoader<IOrderedSynchronizerContribution> CONTRIBUTIONS = ServiceLoader.load(IOrderedSynchronizerContribution.class);
	
	/** The ordered contributions. */
	private static List<IOrderedSynchronizerContribution> orderedContributions = new ArrayList<IOrderedSynchronizerContribution>();
	
	static {
		for (IOrderedSynchronizerContribution next : CONTRIBUTIONS) {
			orderedContributions.add(next);
		}
	}

	
	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (OrderedSynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing Ordered Synchronizers...");}
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						
						Collections.sort(orderedContributions, new Comparator<IOrderedSynchronizerContribution>() {

							@Override
							public int compare(IOrderedSynchronizerContribution contribution1, IOrderedSynchronizerContribution contribution2) {
								int priorityDiff = contribution1.getPriority() - contribution2.getPriority();
								if (priorityDiff == 0) {
									priorityDiff = 1;
								}
								return priorityDiff;
							}
						});
						for (IOrderedSynchronizerContribution next : orderedContributions) {
							try {
								next.synchronize();
							} catch (Exception e) {
								try {
									failedSynchronization(next.getClass().getCanonicalName(), e.getMessage());
								} catch (SchedulerException e1) {
									if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Ordered Synchronizers failed in registering the state log.", e);}
								}
							}
						}
						
						successfulSynchronization(SYNCHRONIZER_NAME, "Details in the previous log messages");
					} else {
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Ordered Synchronizers failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Ordered Synchronizers failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Ordered Synchronizers.");}
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		OrderedSynchronizer synchronizer = new OrderedSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		// TODO Auto-generated method stub
		
	}

}
