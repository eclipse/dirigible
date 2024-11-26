package org.eclipse.dirigible.components.jobs.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionsCountListener extends OpenTelemetryListener {

    private final LongCounter counter;

    JobExecutionsCountListener(OpenTelemetry openTelemetry) {
        this.counter = openTelemetry.getMeter(QuartzMetricConstants.METER_SCOPE_NAME)
                                    .counterBuilder("quartz_job_executed_count")
                                    .setDescription("Total number of executed jobs")
                                    .build();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobKey jobKey = context.getJobDetail()
                               .getKey();

        counter.add(1, Attributes.of(AttributeKey.stringKey("job_name"), jobKey.getName(), AttributeKey.stringKey("job_group"),
                jobKey.getGroup()));
    }
}
