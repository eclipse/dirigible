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

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Security Synchronizer Job Definition Provider.
 */
public class SecuritySynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	/** The Constant DIRIGIBLE_JOB_EXPRESSION_SECURITY. */
	private static final String DIRIGIBLE_JOB_EXPRESSION_SECURITY = "DIRIGIBLE_JOB_EXPRESSION_SECURITY";
	
	/** The Constant DIRIGIBLE_INTERNAL_SECURITY_SYNCHRONIZER_JOB. */
	private static final String DIRIGIBLE_INTERNAL_SECURITY_SYNCHRONIZER_JOB = "dirigible-internal-security-synchronizer-job";
	
	/** The Constant SECURITY_ROLES_AND_ACCESS_SYNCHRONIZER_JOB. */
	static final String SECURITY_ROLES_AND_ACCESS_SYNCHRONIZER_JOB = "Security Roles and Access Synchronizer Job";

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
		jobDefinition.setName(DIRIGIBLE_INTERNAL_SECURITY_SYNCHRONIZER_JOB);
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(SecuritySynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription(SECURITY_ROLES_AND_ACCESS_SYNCHRONIZER_JOB);
		String expression = Configuration.get(DIRIGIBLE_JOB_EXPRESSION_SECURITY, "0/20 * * * * ?");
		jobDefinition.setExpression(expression);
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
