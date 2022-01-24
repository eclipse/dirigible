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
package org.eclipse.dirigible.core.registry.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class RegistrySynchronizerJob extends AbstractSynchronizerJob {

	private RegistrySynchronizer synchronizer = new RegistrySynchronizer();

	@Override
	protected ISynchronizer getSynchronizer() {
		return synchronizer;
	}

	@Override
	public String getName() {
		return RegistrySynchronizerJobDefinitionProvider.REGISTRY_SYNCHRONIZER_JOB;
	}

}
