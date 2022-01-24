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
package org.eclipse.dirigible.runtime.ide.core.synchronizer;

import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

/**
 * The SecuritySynchronizerJob.
 */
public class IDECoreSynchronizerJob extends AbstractSynchronizerJob {

	private static final int TIMEOUT_TIME = 10;

	/** The extensions synchronizer. */
	private IDECoreSynchronizer synchronizer = new IDECoreSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getSynchronizer()
	 */
	@Override
	public ISynchronizer getSynchronizer() {
		return synchronizer;
	}

	@Override
	public String getName() {
		return IDECoreSynchronizerJobDefinitionProvider.IDE_CORE_SYNCHRONIZER_JOB;
	}

	@Override
	protected int getTimeout() {
		return TIMEOUT_TIME;
	}
	
	@Override
	protected TimeUnit getTimeoutUnit() {
		return TimeUnit.MINUTES;
	}
}
