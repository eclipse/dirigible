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
package org.eclipse.dirigible.core.security.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

/**
 * The SecuritySynchronizerJob.
 */
public class SecuritySynchronizerJob extends AbstractSynchronizerJob {

	/** The security synchronizer. */
	private SecuritySynchronizer securitySynchronizer = new SecuritySynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob#getSynchronizer()
	 */
	@Override
	public ISynchronizer getSynchronizer() {
		return securitySynchronizer;
	}

	@Override
	public String getName() {
		return SecuritySynchronizerJobDefinitionProvider.SECURITY_ROLES_AND_ACCESS_SYNCHRONIZER_JOB;
	}

}
