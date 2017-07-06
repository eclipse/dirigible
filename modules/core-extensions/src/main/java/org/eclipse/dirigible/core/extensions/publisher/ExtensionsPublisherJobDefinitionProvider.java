package org.eclipse.dirigible.core.extensions.publisher;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class ExtensionsPublisherJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-extensions-publisher-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(ExtensionsPublisherJob.class.getCanonicalName());
		jobDefinition.setDescription("Extensions publisher Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
