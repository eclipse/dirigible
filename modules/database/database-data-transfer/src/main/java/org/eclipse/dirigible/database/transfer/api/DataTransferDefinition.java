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
package org.eclipse.dirigible.database.transfer.api;

import org.eclipse.dirigible.database.api.DatabaseDefinition;

public class DataTransferDefinition {
	
	private DatabaseDefinition source;
	
	private DatabaseDefinition target;
	
	private DataTransferConfiguration configuration;

	/**
	 * @return the source
	 */
	public DatabaseDefinition getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(DatabaseDefinition source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public DatabaseDefinition getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(DatabaseDefinition target) {
		this.target = target;
	}

	/**
	 * @return the configuration
	 */
	public DataTransferConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(DataTransferConfiguration configuration) {
		this.configuration = configuration;
	}

}
