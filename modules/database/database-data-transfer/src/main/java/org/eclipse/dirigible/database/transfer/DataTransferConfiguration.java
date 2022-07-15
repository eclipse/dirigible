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
package org.eclipse.dirigible.database.transfer;

public class DataTransferConfiguration {
	
	private String sourceSchema;
	
	private String targetSchema;

	/**
	 * @return the sourceSchema
	 */
	public String getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @param sourceSchema the sourceSchema to set
	 */
	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * @return the targetSchema
	 */
	public String getTargetSchema() {
		return targetSchema;
	}

	/**
	 * @param targetSchema the targetSchema to set
	 */
	public void setTargetSchema(String targetSchema) {
		this.targetSchema = targetSchema;
	}

}
