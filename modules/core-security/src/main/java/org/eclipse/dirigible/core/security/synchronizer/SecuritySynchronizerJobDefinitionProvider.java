package org.eclipse.dirigible.core.security.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class SecuritySynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-security-synchronizer-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(SecuritySynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Security Roles and Access Synchronizer Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
