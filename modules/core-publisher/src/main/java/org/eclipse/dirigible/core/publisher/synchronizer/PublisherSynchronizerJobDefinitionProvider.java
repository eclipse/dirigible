package org.eclipse.dirigible.core.publisher.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class PublisherSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-publisher-synchronizer-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(PublisherSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Publisher Synchronizer Job");
		jobDefinition.setExpression("0/10 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
