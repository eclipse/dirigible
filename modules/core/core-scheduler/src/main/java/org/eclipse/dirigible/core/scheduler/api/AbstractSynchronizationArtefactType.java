/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.scheduler.api;

public abstract class AbstractSynchronizationArtefactType implements ISynchronizerArtefactType {
	
	@Override
	public String describeState(int state) {
		String name = null;
		switch (state) {
		case STATE_ARTEFACT_INITIAL:
			name = "Initial";
			break;
		case STATE_ARTEFACT_SUCCESSFUL:
			name = "Successful";
			break;
		case STATE_ARTEFACT_FAILED:
			name = "Failed";
			break;
		case STATE_ARTEFACT_IN_PROGRESS:
			name = "Processing...";
			break;
		case STATE_ARTEFACT_FATAL:
			name = "Erroneous";
			break;
		default:
			name = "Unknown";
			break;
		};
		return name;
	}
	
	public int getStateInitial() {
		return STATE_ARTEFACT_INITIAL;
	}
	
	public int getStateSuccessful() {
		return STATE_ARTEFACT_SUCCESSFUL;
	}
	
	public int getStateFailed() {
		return STATE_ARTEFACT_FAILED;
	}
	
	public int getStateInProgress() {
		return STATE_ARTEFACT_IN_PROGRESS;
	}
	
	public int getStateFatal() {
		return STATE_ARTEFACT_FATAL;
	}

}
