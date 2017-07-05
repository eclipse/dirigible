package org.eclipse.dirigible.core.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerInitializer {
	
	private static SchedulerFactory schedulerFactory = null;
	private static Scheduler scheduler = null;
	
	public static void initialize(LoggingHelper loggingHelper) throws SchedulerException {
		
		initializeScheduler();
		
		JobDefinition jobDefinition = testDefinition();
		
		scheduleJob(loggingHelper, jobDefinition);
		
		startScheduler();
		
	}

	private static void startScheduler() throws SchedulerException {
		try {
			scheduler.start();
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	private static JobDefinition testDefinition() {
		JobDefinition jobDefinition = new JobDefinition(); 
		jobDefinition.setName("dirigible-internal-tasks-runner-short");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(InternalJob.class.getCanonicalName());
		jobDefinition.setDescription("Internal Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(false);
		return jobDefinition;
	}

	private static void initializeScheduler() throws SchedulerException {
		try {
			synchronized (SchedulerInitializer.class) {
				if (scheduler == null) {
					schedulerFactory = new StdSchedulerFactory();
					scheduler = schedulerFactory.getScheduler();
				}
			}
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	private static void scheduleJob(LoggingHelper loggingHelper, JobDefinition jobDefinition)
			throws SchedulerException {
		try {
			Class<Job> jobClass = (Class<Job>) Class.forName(jobDefinition.getClazz()); 
			JobDetail job = newJob(jobClass)
			    .withIdentity(jobDefinition.getName(), jobDefinition.getGroup())
			    .withDescription(jobDefinition.getDescription())
			    .build();
		
			CronTrigger trigger = newTrigger()
				    .withIdentity(jobDefinition.getName(), jobDefinition.getGroup())
				    .withSchedule(cronSchedule(jobDefinition.getExpression()))
				    .build();
			
			scheduler.scheduleJob(job, trigger);
			
			loggingHelper.info("Scheduled Job: [{}] of group: [{}] at: [{}]", jobDefinition.getName(), jobDefinition.getGroup(), jobDefinition.getExpression());
			
		} catch (ObjectAlreadyExistsException e) {
			loggingHelper.warn(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new SchedulerException("Invalid class name for the job", e);
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}
	
	public static void shutdown(LoggingHelper loggingHelper) throws SchedulerException {
		synchronized (SchedulerInitializer.class) {
			if (scheduler == null) {
				throw new SchedulerException("Scheduler has not been initialized and strated.");
			}
			try {
				scheduler.shutdown(true);
			} catch (org.quartz.SchedulerException e) {
				throw new SchedulerException(e);
			}
		}
	}

}
