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

public interface ISynchronizerArtefactType {
	
	/** The state initial */
	public int STATE_ARTEFACT_INITIAL = 0;
	
	/** The state successful */
	public int STATE_ARTEFACT_SUCCESSFUL = 1;
	
	/** The state failed */
	public int STATE_ARTEFACT_FAILED = 2;
	
	/** The state in progress */
	public int STATE_ARTEFACT_IN_PROGRESS = 3;
	
	/** The state fatal */
	public int STATE_ARTEFACT_FATAL = 4;
	
	public int getId();
	
	public String getName();
	
	public String describeState(int state);
	
}
