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
package org.eclipse.dirigible.cms.csvim.synchronizer;

import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

/**
 * The Class CSVIM SynchronizerJob.
 */
public class CsvimSynchronizerJob extends AbstractSynchronizerJob {

	private static final int TIMEOUT_TIME_IN_MINUTES = 10;

	private CsvimSynchronizer csvimSynchronizer = new CsvimSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getSynchronizer()
	 */
	@Override
	public ISynchronizer getSynchronizer() {
		return csvimSynchronizer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getName()
	 */
	@Override
	public String getName() {
		return CsvimSynchronizerJobDefinitionProvider.CSVIM_SYNCHRONIZER_JOB;
	}

	@Override
	protected int getTimeout() {
		return TIMEOUT_TIME_IN_MINUTES;
	}

	@Override
	protected TimeUnit getTimeoutUnit() {
		return TimeUnit.MINUTES;
	}
}
