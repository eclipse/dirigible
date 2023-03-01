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
package org.eclipse.dirigible.core.scheduler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eclipse.dirigible.core.scheduler.api.ISynchronizerCoreService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SynchronizerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.SynchronizerStateDefinition;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class JobCoreServiceTest.
 */
public class SynchronizerCoreServiceTest extends AbstractDirigibleTest {

	/** The job core service. */
	private ISynchronizerCoreService synchronizerCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.synchronizerCoreService = new SynchronizerCoreService();
	}

	/**
	 * Creates the synchronizer state.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void createSynchronizerState() throws SchedulerException {
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
		synchronizerCoreService.createSynchronizerState("test_synchronizer_state1", 1, "Test", 0, 0, 0, 0);
		List<SynchronizerStateDefinition> list = synchronizerCoreService.getSynchronizerStates();
		assertEquals(1, list.size());
		SynchronizerStateDefinition synchronizerStateDefinition = list.get(0);
		System.out.println(synchronizerStateDefinition.toString());
		assertEquals("test_synchronizer_state1", synchronizerStateDefinition.getName());
		assertEquals("Test", synchronizerStateDefinition.getMessage());
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
	}

	/**
	 * Gets the synchronizer state.
	 *
	 * @return the synchronizer state
	 * @throws SchedulerException             the scheduler exception
	 */
	@Test
	public void getSynchronizerState() throws SchedulerException {
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
		synchronizerCoreService.createSynchronizerState("test_synchronizer_state1", 1, "Test", 0, 0, 0, 0);
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState("test_synchronizer_state1");
		assertEquals("test_synchronizer_state1", synchronizerStateDefinition.getName());
		assertEquals("Test", synchronizerStateDefinition.getMessage());
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
	}

	/**
	 * Update synchronizer state.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void updatetSynchronizerState() throws SchedulerException {
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
		synchronizerCoreService.createSynchronizerState("test_synchronizer_state1", 1, "Test", 0, 0, 0, 0);
		SynchronizerStateDefinition synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState("test_synchronizer_state1");
		assertEquals("test_synchronizer_state1", synchronizerStateDefinition.getName());
		assertEquals("Test", synchronizerStateDefinition.getMessage());
		synchronizerCoreService.updateSynchronizerState("test_synchronizer_state1", 1, "Test 2", 0, 0, 0, 0);
		synchronizerStateDefinition = synchronizerCoreService.getSynchronizerState("test_synchronizer_state1");
		assertEquals("test_synchronizer_state1", synchronizerStateDefinition.getName());
		assertEquals("Test 2", synchronizerStateDefinition.getMessage());
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
	}

	/**
	 * Removes the synchronizer state.
	 *
	 * @throws SchedulerException
	 *             the scheduler exception
	 */
	@Test
	public void removeSynchronizerState() throws SchedulerException {
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
		synchronizerCoreService.createSynchronizerState("test_synchronizer_state1", 1, "Test", 0, 0, 0, 0);
		SynchronizerStateDefinition SynchronizerStateDefinition = synchronizerCoreService.getSynchronizerState("test_synchronizer_state1");
		assertEquals("test_synchronizer_state1", SynchronizerStateDefinition.getName());
		assertEquals("Test", SynchronizerStateDefinition.getMessage());
		synchronizerCoreService.removeSynchronizerState("test_synchronizer_state1");
		SynchronizerStateDefinition = synchronizerCoreService.getSynchronizerState("test_synchronizer_state1");
		assertNull(SynchronizerStateDefinition);
	}

}
