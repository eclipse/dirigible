package org.eclipse.dirigible.core.scheduler.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public interface ISchedulerCoreService extends ICoreService {
	
	public JobDefinition createJob(String name, String group, String clazz, String description, String expression, boolean singleton) throws SchedulerException;
	
	public JobDefinition createJob(JobDefinition jobDefinition) throws SchedulerException;

	public JobDefinition getJob(String name) throws SchedulerException;

	public void removeJob(String name) throws SchedulerException;

	public void updateJob(String name, String group, String clazz, String description, String expression, boolean singleton) throws SchedulerException;

	public List<JobDefinition> getJobs() throws SchedulerException;

}