package org.eclipse.dirigible.core.scheduler.manager;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerManager {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static SchedulerFactory schedulerFactory = null;
	private static Scheduler scheduler = null;

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static void createScheduler() throws SchedulerException {
		try {
			synchronized (SchedulerInitializer.class) {
				if (scheduler == null) {
					Configuration.load("/dirigible-scheduler.properties");
					Properties quartzProperties = new Properties();
					String quartzConfig = Configuration.get("DIRIGIBLE_SCHEDULER_QUARTZ_PROPERTIES");
					if ((quartzConfig != null) && "".equals(quartzConfig.trim()) && !quartzConfig.startsWith("classpath")) {
						quartzProperties.load(new FileReader(quartzConfig));
					} else {
						quartzProperties.load(SchedulerManager.class.getResourceAsStream("/quartz.properties"));
					}
					schedulerFactory = new StdSchedulerFactory(quartzProperties);
					scheduler = schedulerFactory.getScheduler();
					String message = "Scheduler has been created.";
					logger.info(message);
				}
			}
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removeScheduler() throws SchedulerException {
		shutdownScheduler();
	}

	public static void shutdownScheduler() throws SchedulerException {
		synchronized (SchedulerInitializer.class) {
			if (scheduler == null) {
				throw new SchedulerException("Scheduler has not been initialized and strated.");
			}
			try {
				scheduler.shutdown(true);
				String message = "Scheduler has been shutted down.";
				logger.info(message);
				scheduler = null;
			} catch (org.quartz.SchedulerException e) {
				throw new SchedulerException(e);
			}
		}
	}

	public static void startScheduler() throws SchedulerException {
		synchronized (SchedulerInitializer.class) {
			if (scheduler == null) {
				throw new SchedulerException("Scheduler has not been initialized.");
			}
			try {
				scheduler.start();
				logger.info("Scheduler has been started.");
			} catch (org.quartz.SchedulerException e) {
				throw new SchedulerException(e);
			}
		}
	}

	public static void scheduleJob(JobDefinition jobDefinition) throws SchedulerException {
		try {
			JobKey jobKey = new JobKey(jobDefinition.getName(), jobDefinition.getGroup());
			TriggerKey triggerKey = new TriggerKey(jobDefinition.getName(), jobDefinition.getGroup());
			if (!scheduler.checkExists(jobKey) && (!scheduler.checkExists(triggerKey))) {
				Class<Job> jobClass = (Class<Job>) Class.forName(jobDefinition.getClazz());
				JobDetail job = newJob(jobClass).withIdentity(jobKey).withDescription(jobDefinition.getDescription()).build();

				CronTrigger trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(jobDefinition.getExpression())).build();

				scheduler.scheduleJob(job, trigger);

				logger.info("Scheduled Job: [{}] of group: [{}] at: [{}]", jobDefinition.getName(), jobDefinition.getGroup(),
						jobDefinition.getExpression());
			}
		} catch (ObjectAlreadyExistsException e) {
			logger.warn(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new SchedulerException("Invalid class name for the job", e);
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	public static void unscheduleJob(String name, String group) throws SchedulerException {
		try {
			JobKey jobKey = new JobKey(name, group);
			TriggerKey triggerKey = new TriggerKey(name, group);
			if (scheduler.checkExists(triggerKey)) {
				scheduler.unscheduleJob(triggerKey);
				scheduler.deleteJob(jobKey);
				logger.info("Unscheduled Job: [{}] of group: [{}]", name, group);
			}
		} catch (ObjectAlreadyExistsException e) {
			logger.warn(e.getMessage());
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

	public static Set<TriggerKey> listJobs() throws SchedulerException {
		try {
			Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
			return triggerKeys;
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e);
		}
	}

}
