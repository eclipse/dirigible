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
package org.eclipse.dirigible.core.security.artefacts;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizationArtefactType;

public class AccessSynchronizationArtefactType extends AbstractSynchronizationArtefactType {

	@Override
	public String getId() {
		return "Security Access";
	}

	@Override
	protected String getArtefactStateMessage(ArtefactState state) {
		switch (state) {
		case FAILED_CREATE:
			return "Create security access has failed";
		case FAILED_CREATE_UPDATE:
			return "Create or update security access has failed";
		case FAILED_UPDATE:
			return "Update security access has failed";
		case SUCCESSFUL_CREATE:
			return "Create security access was successful";
		case SUCCESSFUL_CREATE_UPDATE:
			return "Create or update security access was successful";
		case SUCCESSFUL_UPDATE:
			return "Update security access was successful";
		default:
			return state.getValue();
		}
	}

}
