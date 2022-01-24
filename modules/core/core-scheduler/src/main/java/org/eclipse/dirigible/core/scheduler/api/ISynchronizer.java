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
package org.eclipse.dirigible.core.scheduler.api;

/**
 * The Synchronizer interface.
 */
public interface ISynchronizer {
	
	public static final String DIRIGIBLE_SYNCHRONIZER_IGNORE_DEPENDENCIES = "DIRIGIBLE_SYNCHRONIZER_IGNORE_DEPENDENCIES";

	/**
	 * Synchronize.
	 */
	public void synchronize();
	
	/**
	 * Before synchronizing.
	 * 
	 * @return to continue or not
	 */
	public boolean beforeSynchronizing();
	
	/**
	 * After synchronizing.
	 */
	public void afterSynchronizing();

	/**
	 * Set the flag whether the synchronization is forced or not
	 * 
	 * @param forced flag showing whether is forced or not
	 */
	public void setForcedSynchronization(boolean forced);
	
	/**
	 * Is synchronization enabled globally
	 * 
	 * @return true if enabled
	 */
	public boolean isSynchronizationEnabled();

}
