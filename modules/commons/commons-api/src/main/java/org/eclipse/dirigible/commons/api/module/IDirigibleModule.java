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
package org.eclipse.dirigible.commons.api.module;

/**
 * The Interface DirigibleModule.
 */
public interface IDirigibleModule {
	
	/** The priority configuration. */
	int PRIORITY_CONFIGURATION = 10;
	
	/** The priority database. */
	int PRIORITY_DATABASE = 20;
	
	/** The priority repository. */
	int PRIORITY_REPOSITORY = 30;
	
	/** The priority engine. */
	int PRIORITY_ENGINE = 40;
	
	/** The priority default. */
	int PRIORITY_DEFAULT = 50;
	
	/** The low priority. */
	int LOW_PRIORITY = 100;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	int getPriority();
	
	/**
	 * Runs the module initialization logic.
	 */
	void configure();

}