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
package org.eclipse.dirigible.core.registry.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class RegistrySynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-registry-synchronizer-job");
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(RegistrySynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Registry Synchronizer Job");
		jobDefinition.setExpression("0 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
