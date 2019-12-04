/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

/**
 * The Class ExtensionsSynchronizerJobDefinitionProvider.
 */
public class ODataSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider#getJobDefinition()
	 */
	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-odata-synchronizer-job");
		jobDefinition.setGroup(ISchedulerCoreService.JOB_GROUP_INTERNAL);
		jobDefinition.setClazz(ODataSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("OData Synchronizer Job");
		jobDefinition.setExpression("0/25 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
