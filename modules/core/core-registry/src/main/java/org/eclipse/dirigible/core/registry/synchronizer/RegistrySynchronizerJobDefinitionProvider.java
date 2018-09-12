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
