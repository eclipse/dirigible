/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.scheduler.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateLogDefinition;

/**
 * The Synchronizer Core Service interface.
 */
public interface ISynchronizerCoreService extends ICoreService {
	
	/** The state initial */
	public int STATE_INITIAL = 0;
	
	/** The state successful */
	public int STATE_SUCCESSFUL = 1;
	
	/** The state failed */
	public int STATE_FAILED = 2;
	
	/** The state in progress */
	public int STATE_IN_PROGRESS = 3;

	/**
	 * Creates the synchronizer state with parameters.
	 * 
	 * @param name the name of the synchronizer
	 * @param state the last state
	 * @param message the message from execution
	 * @param firstTimeTriggered timestamp first time triggered
	 * @param firstTimeFinished timestamp first time finished
	 * @param lastTimeTriggered timestamp last time triggered
	 * @param lastTimeFinished timestamp last time finished
	 * @return the synchronizer state
	 * @throws SchedulerException the scheduler exception
	 */
	public SynchronizerStateDefinition createSynchronizerState(String name, int state, String message, long firstTimeTriggered, long firstTimeFinished, long lastTimeTriggered, long lastTimeFinished) throws SchedulerException;

	/**
	 * Creates the synchronizer state by definition.
	 *
	 * @param synchronizerStateDefinition
	 *            the synchronizer state definition
	 * @return the synchronizer state
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public SynchronizerStateDefinition createSynchronizerState(SynchronizerStateDefinition synchronizerStateDefinition) throws SchedulerException;

	/**
	 * Gets the synchronizer state.
	 *
	 * @param name
	 *            the name
	 * @return the synchronizer state
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public SynchronizerStateDefinition getSynchronizerState(String name) throws SchedulerException;

	/**
	 * Removes the synchronizer state.
	 *
	 * @param name
	 *            the name
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public void removeSynchronizerState(String name) throws SchedulerException;

	/**
	 * Update synchronizer state.
	 *
	 * @param name the name of the synchronizer
	 * @param state the last state
	 * @param message the message from execution
	 * @param firstTimeTriggered timestamp first time triggered
	 * @param firstTimeFinished timestamp first time finished
	 * @param lastTimeTriggered timestamp last time triggered
	 * @param lastTimeFinished timestamp last time finished
	 * @throws SchedulerException the scheduler exception
	 */
	public void updateSynchronizerState(String name, int state, String message, long firstTimeTriggered, long firstTimeFinished, long lastTimeTriggered, long lastTimeFinished) throws SchedulerException;
	
	/**
	 * Update synchronizer state.
	 * 
	 * @param synchronizerStateDefinition the definition
	 * @throws SchedulerException the scheduler exception
	 */
	public void updateSynchronizerState(SynchronizerStateDefinition synchronizerStateDefinition) throws SchedulerException;

	/**
	 * Gets the synchronizer states.
	 *
	 * @return the synchronizer states
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	public List<SynchronizerStateDefinition> getSynchronizerStates() throws SchedulerException;

	/**
	 * Checks whether a job with the given name already exist
	 *
	 * @param name
	 *            the name
	 * @return true if exists and false otherwise
	 * @throws SchedulerException
	 *             in case of an internal error
	 */
	public boolean existsSynchronizerState(String name) throws SchedulerException;

	/**
	 * Get all the state logs by a given synchronizer
	 * @param name the synchronizer
	 * @return the list of state log
	 * @throws SchedulerException the scheduler exception
	 */
	public List<SynchronizerStateLogDefinition> getSynchronizerStateLogs(String name) throws SchedulerException;

	/**
	 * Delete old state log
	 * 
	 * @throws SchedulerException the scheduler exception
	 */
	public void deleteOldSynchronizerStateLogs() throws SchedulerException;

}
