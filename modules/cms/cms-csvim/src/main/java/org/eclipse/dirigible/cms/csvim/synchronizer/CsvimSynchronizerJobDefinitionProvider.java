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

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Class CSVIM SynchronizerJobDefinitionProvider.
 */
public class CsvimSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	private static final String DIRIGIBLE_JOB_EXPRESSION_CSVIM = "DIRIGIBLE_JOB_EXPRESSION_CSVIM";
	
	private static final String DIRIGIBLE_INTERNAL_CSVIM_SYNCHRONIZER_JOB = "dirigible-internal-csvim-synchronizer-job";
	
	static final String CSVIM_SYNCHRONIZER_JOB = "CSVIM Synchronizer Job";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider#getJobDefinition()
	 */
	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName(DIRIGIBLE_INTERNAL_CSVIM_SYNCHRONIZER_JOB);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(CsvimSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription(CSVIM_SYNCHRONIZER_JOB);
		String expression = Configuration.get(DIRIGIBLE_JOB_EXPRESSION_CSVIM, "0/55 * * * * ?");
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
