package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.service.SchedulerCoreService;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class JobsProcessor {
	
	@Inject
	private SchedulerCoreService schedulerCoreService;
	
	public String list() throws SchedulerException {
		
		List<JobDefinition> jobs = schedulerCoreService.getJobs();
		
        return GsonHelper.GSON.toJson(jobs);
	}


}
