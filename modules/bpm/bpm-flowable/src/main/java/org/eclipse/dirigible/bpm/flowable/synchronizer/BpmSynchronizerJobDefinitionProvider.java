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
package org.eclipse.dirigible.bpm.flowable.synchronizer;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Class BPMSynchronizerJobDefinitionProvider.
 */
public class BpmSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {
	
	/** The Constant DIRIGIBLE_JOB_EXPRESSION_BPM. */
	private static final String DIRIGIBLE_JOB_EXPRESSION_BPM = "DIRIGIBLE_JOB_EXPRESSION_BPM";
	
	/** The Constant DIRIGIBLE_INTERNAL_BPM_SYNCHRONIZER_JOB. */
	private static final String DIRIGIBLE_INTERNAL_BPM_SYNCHRONIZER_JOB = "dirigible-internal-bpm-synchronizer-job";
	
	/** The Constant BPM_SYNCHRONIZER_JOB. */
	static final String BPM_SYNCHRONIZER_JOB = "BPM Synchronizer Job";

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
		jobDefinition.setName(DIRIGIBLE_INTERNAL_BPM_SYNCHRONIZER_JOB);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(BpmSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription(BPM_SYNCHRONIZER_JOB);
		String expression = Configuration.get(DIRIGIBLE_JOB_EXPRESSION_BPM, "0/50 * * * * ?");
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
