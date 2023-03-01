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
package org.eclipse.dirigible.core.publisher.synchronizer;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The publisher synchronizer job definition provider.
 */
public class PublisherSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	/** The Constant DIRIGIBLE_JOB_EXPRESSION_PUBLISHER. */
	private static final String DIRIGIBLE_JOB_EXPRESSION_PUBLISHER = "DIRIGIBLE_JOB_EXPRESSION_PUBLISHER";
	
	/** The Constant DIRIGIBLE_INTERNAL_PUBLISHER_SYNCHRONIZER_JOB. */
	private static final String DIRIGIBLE_INTERNAL_PUBLISHER_SYNCHRONIZER_JOB = "dirigible-internal-publisher-synchronizer-job";
	
	/** The Constant PUBLISHER_SYNCHRONIZER_JOB. */
	static final String PUBLISHER_SYNCHRONIZER_JOB = "Publisher Synchronizer Job";

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
		jobDefinition.setName(DIRIGIBLE_INTERNAL_PUBLISHER_SYNCHRONIZER_JOB);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(PublisherSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription(PUBLISHER_SYNCHRONIZER_JOB);
		String expression = Configuration.get(DIRIGIBLE_JOB_EXPRESSION_PUBLISHER, "0/5 * * * * ?");
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
