package org.eclipse.dirigible.core.scheduler.api;

import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public interface IJobDefinitionProvider {
	
	public JobDefinition getJobDefinition();

}
