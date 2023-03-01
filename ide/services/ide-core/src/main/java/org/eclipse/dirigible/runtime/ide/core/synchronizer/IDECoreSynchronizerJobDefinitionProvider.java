/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.core.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Security Synchronizer Job Definition Provider.
 */
public class IDECoreSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	/** The Constant DIRIGIBLE_INTERNAL_IDE_CORE_SYNCHRONIZER_JOB. */
	private static final String DIRIGIBLE_INTERNAL_IDE_CORE_SYNCHRONIZER_JOB = "dirigible-internal-ide-core-synchronizer-job";
	
	/** The Constant IDE_CORE_SYNCHRONIZER_JOB. */
	static final String IDE_CORE_SYNCHRONIZER_JOB = "IDE Core Synchronizer Job";

	/**
	 * Gets the job definition.
	 *
	 * @return the job definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider#getJobDefinition()
	 */
	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(DIRIGIBLE_INTERNAL_IDE_CORE_SYNCHRONIZER_JOB);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(IDECoreSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription(IDE_CORE_SYNCHRONIZER_JOB);
		jobDefinition.setExpression("");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
