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

	/** The Constant TIMEOUT_TIME_IN_MINUTES. */
	private static final int TIMEOUT_TIME_IN_MINUTES = 10;

	/** The csvim synchronizer. */
	private CsvimSynchronizer csvimSynchronizer = new CsvimSynchronizer();

	/**
	 * Gets the synchronizer.
	 *
	 * @return the synchronizer
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getSynchronizer()
	 */
	@Override
	public ISynchronizer getSynchronizer() {
		return csvimSynchronizer;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getName()
	 */
	@Override
	public String getName() {
		return CsvimSynchronizerJobDefinitionProvider.CSVIM_SYNCHRONIZER_JOB;
	}

	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	@Override
	protected int getTimeout() {
		return TIMEOUT_TIME_IN_MINUTES;
	}

	/**
	 * Gets the timeout unit.
	 *
	 * @return the timeout unit
	 */
	@Override
	protected TimeUnit getTimeoutUnit() {
		return TimeUnit.MINUTES;
	}
}
