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
package org.eclipse.dirigible.database.changelog.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

/**
 * The Changelog Synchronizer Job.
 */
public class ChangelogSynchronizerJob extends AbstractSynchronizerJob {

	/** The changelog synchronizer. */
	private ChangelogSynchronizer changelogSynchronizer = new ChangelogSynchronizer();

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
		return changelogSynchronizer;
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
		return ChangelogSynchronizerJobDefinitionProvider.CHANGELOG_SYNCHRONIZER_JOB;
	}

}
