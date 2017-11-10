/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

// TODO: Auto-generated Javadoc
/**
 * The Class DataStructuresSynchronizerJobDefinitionProvider.
 */
public class DataStructuresSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider#getJobDefinition()
	 */
	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-data-structures-synchronizer-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(DataStructuresSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Data Structures Synchronizer Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
