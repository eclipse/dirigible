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
package org.eclipse.dirigible.database.ds.artefacts;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizationArtefactType;

/**
 * The Class SchemaSynchronizationArtefactType.
 */
public class SchemaSynchronizationArtefactType extends AbstractSynchronizationArtefactType {

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return "Database Schema Layout";
	}

	/**
	 * Gets the artefact state message.
	 *
	 * @param state the state
	 * @return the artefact state message
	 */
	@Override
	protected String getArtefactStateMessage(ArtefactState state) {
		switch (state) {
		case FAILED_CREATE:
			return "Processing for create database schema layout has failed";
		case FAILED_CREATE_UPDATE:
			return "Processing for create or update database schema layout has failed";
		case FAILED_UPDATE:
			return "Processing for update database schema layout has failed";
		case SUCCESSFUL_CREATE:
			return "Processing for create database schema layout was successful";
		case SUCCESSFUL_CREATE_UPDATE:
			return "Processing Create or update database schema layout was successful";
		case SUCCESSFUL_UPDATE:
			return "Processing for update database schema layout was successful";
		default:
			return state.getValue();
		}
	}

}
