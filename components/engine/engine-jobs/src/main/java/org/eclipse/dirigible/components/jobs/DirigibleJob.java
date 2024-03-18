package org.eclipse.dirigible.components.jobs;

import org.quartz.*;

import java.util.Optional;

public abstract class DirigibleJob implements Job {

    public Trigger createTrigger() {
        JobDetail job = createJob();

        Optional<String> group = getTriggerGroup();
        TriggerKey key = group.isPresent() ? TriggerKey.triggerKey(getTriggerKey(), group.get()) : TriggerKey.triggerKey(getTriggerKey());

        return TriggerBuilder.newTrigger()
                             .forJob(job)
                             .withIdentity(key)
                             .withDescription(getTriggerDescription())
                             .withSchedule(getSchedule())
                             .build();
    }

    public JobDetail createJob() {
        Optional<String> jobGroup = getJobGroup();
        JobKey key = jobGroup.isPresent() ? JobKey.jobKey(getJobKey(), jobGroup.get()) : JobKey.jobKey(getJobKey());

        return JobBuilder.newJob()
                         .ofType(this.getClass())
                         .storeDurably()
                         .withIdentity(key)
                         .withDescription(getJobDescription())
                         .build();
    }

    protected abstract Optional<String> getTriggerGroup();

    protected abstract String getTriggerKey();

    protected abstract String getTriggerDescription();

    protected abstract SimpleScheduleBuilder getSchedule();

    protected abstract Optional<String> getJobGroup();

    protected abstract String getJobKey();

    protected abstract String getJobDescription();
}
