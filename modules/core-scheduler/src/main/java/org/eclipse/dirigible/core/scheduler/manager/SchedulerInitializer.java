package org.eclipse.dirigible.core.scheduler.manager;

import static java.text.MessageFormat.format;

import java.util.ServiceLoader;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class SchedulerInitializer {
	
	@Inject
	private SchedulerCoreService schedulerCoreService;
	
	public void initialize(LoggingHelper loggingHelper) throws SchedulerException {
		
		loggingHelper.beginSection("Initializing Job Scheduler Service...");
		
		initializeScheduler(loggingHelper);
		
		// schedule the System Job
		scheduleSystemJob(loggingHelper);
		
		// enumerate the Internal Jobs
		scheduleInternalJobs(loggingHelper);
		
		startScheduler(loggingHelper);
		
		loggingHelper.endSection("Done initializing Job Scheduler Service.");
	}

	private void scheduleInternalJobs(LoggingHelper loggingHelper) {
		loggingHelper.beginSection("Initializing the Internal Jobs...");
		ServiceLoader<IJobDefinitionProvider> jobDefinitionProviders = ServiceLoader.load(IJobDefinitionProvider.class);
		for (IJobDefinitionProvider next : jobDefinitionProviders) {
			JobDefinition jobDefinition = next.getJobDefinition();
			loggingHelper.beginGroup(format("Initializing the Internal Job [{0}] in group [{1}]...", jobDefinition.getName(), jobDefinition.getGroup()));
			try {
				JobDefinition found = schedulerCoreService.getJob(jobDefinition.getName());
				if (found == null) {
					schedulerCoreService.createJob(jobDefinition);
					scheduleJob(jobDefinition);
				}
			} catch (Throwable e) {
				loggingHelper.error(format("Failed installing Internal Job [{0}] in group [{1}].", jobDefinition.getName(), jobDefinition.getGroup()), e);
			}
			loggingHelper.endGroup(format("Done installing Internal Job [{0}] in group [{1}].", jobDefinition.getName(), jobDefinition.getGroup()));
		}
		loggingHelper.endSection("Done initializing the Internal Jobs.");
		
	}

	private void scheduleSystemJob(LoggingHelper loggingHelper) throws SchedulerException {
		loggingHelper.beginGroup(format("Initializing the System Job ..."));
		JobDefinition systemJobDefinition = SystemJob.getSystemJobDefinition();
		scheduleJob(systemJobDefinition);
		loggingHelper.endGroup(format("Done initializing the System Job."));
	}

	private void scheduleJob(JobDefinition jobDefinition) throws SchedulerException {
		SchedulerManager.scheduleJob(jobDefinition);
	}

	private void startScheduler(LoggingHelper loggingHelper) throws SchedulerException {
		SchedulerManager.startScheduler(loggingHelper);
	}

	private void initializeScheduler(LoggingHelper loggingHelper) throws SchedulerException {
		SchedulerManager.createScheduler(loggingHelper);
	}

	public static void shutdown(LoggingHelper loggingHelper) throws SchedulerException {
		SchedulerManager.shutdownScheduler(loggingHelper);
	}
	
	

}
