package org.eclipse.dirigible.core.extensions.publisher;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ExtensionsPublisherJob implements Job {
	
	private ExtensionsPublisher extensionsPublisher = StaticInjector.getInjector().getInstance(ExtensionsPublisher.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println("Hello from the Extensions Publisher Job!");
		
		extensionsPublisher.publishAll();
	}

}
