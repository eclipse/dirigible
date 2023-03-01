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
package org.eclipse.dirigible.core.scheduler.api;

/**
 * The Interface ISynchronizerJob.
 */
public interface ISynchronizerJob {
	
	/**
	 * Getter for the name of the Synchronizer Job.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the synchronizer.
	 *
	 * @return the synchronizer
	 */
	public ISynchronizer getSynchronizer();

}
