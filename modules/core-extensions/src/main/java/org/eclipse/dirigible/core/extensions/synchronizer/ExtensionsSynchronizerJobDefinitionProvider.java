package org.eclipse.dirigible.core.extensions.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class ExtensionsSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-extensions-synchronizer-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(ExtensionsSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Extensions Synchronizer Job");
		jobDefinition.setExpression("0 */5 * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
