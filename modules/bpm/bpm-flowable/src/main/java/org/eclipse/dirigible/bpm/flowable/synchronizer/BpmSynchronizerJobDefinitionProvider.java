/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
	
	private static final String DIRIGIBLE_JOB_EXPRESSION_BPM = "DIRIGIBLE_JOB_EXPRESSION_BPM";
	private static final String BPM_SYNCHRONIZER_JOB = "BPM Synchronizer Job";
	private static final String DIRIGIBLE_INTERNAL_BPM_SYNCHRONIZER_JOB = "dirigible-internal-bpm-synchronizer-job";

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
		String expression = Configuration.get(DIRIGIBLE_JOB_EXPRESSION_BPM, "0/30 * * * * ?");
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
