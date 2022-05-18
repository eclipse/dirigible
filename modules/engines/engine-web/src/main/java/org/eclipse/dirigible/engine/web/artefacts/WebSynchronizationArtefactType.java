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
package org.eclipse.dirigible.engine.web.artefacts;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizationArtefactType;

public class WebSynchronizationArtefactType extends AbstractSynchronizationArtefactType {

	@Override
	public String getId() {
		return "Web";
	}

	@Override
	protected String getArtefactStateMessage(ArtefactState state) {
		switch (state) {
		case FAILED_CREATE:
			return "Create web exposure has failed";
		case FAILED_CREATE_UPDATE:
			return "Create or update web exposure has failed";
		case FAILED_UPDATE:
			return "Update web exposure has failed";
		case FAILED_DELETE:
			return "Delete web exposure has failed";
		case SUCCESSFUL_CREATE:
			return "Create web exposure was successful";
		case SUCCESSFUL_CREATE_UPDATE:
			return "Create or update web exposure was successful";
		case SUCCESSFUL_UPDATE:
			return "Update web exposure was successful";
		case SUCCESSFUL_DELETE:
			return "Delete web exposure was successful";
		default:
			return state.getValue();
		}
	}

}
