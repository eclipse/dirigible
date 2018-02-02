package org.eclipse.dirigible.core.scheduler.handler;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.scheduler.api.ISchedulerCoreService;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * The built-in scripting service job handler
 */
public class JobHandler implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String module = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_HANDLER);
		String type = (String) context.getJobDetail().getJobDataMap().get(ISchedulerCoreService.JOB_PARAMETER_ENGINE);
		try {
			if (type == null) {
				type = "javascript";
			}
			ScriptEngineExecutorsManager.executeServiceModule(type, module, null);
		} catch (ScriptingException e) {
			throw new JobExecutionException(e);
		}
	}

}
