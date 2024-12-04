package org.eclipse.dirigible.components.jobs.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
class RunningJobsCountMetricsConfigurator implements ApplicationListener<ApplicationReadyEvent> {

    private final OpenTelemetry openTelemetry;
    private final Scheduler scheduler;

    RunningJobsCountMetricsConfigurator(OpenTelemetry openTelemetry, Scheduler scheduler) {
        this.openTelemetry = openTelemetry;
        this.scheduler = scheduler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Meter meter = openTelemetry.getMeter(QuartzMetricConstants.METER_SCOPE_NAME);

        meter.gaugeBuilder("quartz_scheduler_running_jobs")
             .setDescription("Current number of running jobs")
             .ofLongs()
             .buildWithCallback(observation -> {
                 try {
                     observation.record(scheduler.getCurrentlyExecutingJobs()
                                                 .size());
                 } catch (SchedulerException e) {
                     observation.record(0);
                 }
             });
    }

}
