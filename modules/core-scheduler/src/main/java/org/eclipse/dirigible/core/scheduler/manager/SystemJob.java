package org.eclipse.dirigible.core.scheduler.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

public class SystemJob implements Job {

	private static final String SYSTEM_JOB_NAME = "dirigible-system-job";
	private static final String SYSTEM_GROUP = "dirigible-system";
	
	private SchedulerCoreService schedulerCoreService = StaticInjector.getInjector().getInstance(SchedulerCoreService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			// take all the jobs from the database and schedule them
			List<JobKey> jobs = new ArrayList<JobKey>();
			jobs.add(new JobKey(SYSTEM_JOB_NAME, SYSTEM_GROUP));
			List<JobDefinition> jobDefinitions = schedulerCoreService.getJobs();
			for (JobDefinition jobDefinition : jobDefinitions) {
				SchedulerManager.scheduleJob(jobDefinition);
				JobKey jobKey = new JobKey(jobDefinition.getName(), jobDefinition.getGroup());
				jobs.add(jobKey);
			}
			// remove all the already scheduled, but removed from the database
			Set<TriggerKey> triggers = SchedulerManager.listJobs();
			for (TriggerKey triggerKey : triggers) {
				JobKey jobKey = new JobKey(triggerKey.getName(), triggerKey.getGroup());
				if (!jobs.contains(jobKey)) {
					SchedulerManager.unscheduleJob(triggerKey.getName(), triggerKey.getGroup());
				}
			}
		} catch (SchedulerException e) {
			throw new JobExecutionException(e);
		}
		
	}
	
	public static JobDefinition getSystemJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition(); 
		jobDefinition.setName(SYSTEM_JOB_NAME);
		jobDefinition.setGroup(SYSTEM_GROUP);
		jobDefinition.setClazz(SystemJob.class.getCanonicalName());
		jobDefinition.setDescription("System Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(false);
		return jobDefinition;
	}

}
