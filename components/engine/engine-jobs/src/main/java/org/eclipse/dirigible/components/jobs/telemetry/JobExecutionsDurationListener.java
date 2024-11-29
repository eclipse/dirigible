package org.eclipse.dirigible.components.jobs.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JobExecutionsDurationListener extends OpenTelemetryListener {

    private static final String START_TIME_KEY = "startTime";

    private final LongHistogram histogram;

    JobExecutionsDurationListener(OpenTelemetry openTelemetry) {
        this.histogram = openTelemetry.getMeter(QuartzMetricConstants.METER_SCOPE_NAME)
                                      .histogramBuilder("quartz_job_execution_time")
                                      .setDescription("Job execution duration in milliseconds")
                                      .ofLongs()
                                      .build();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.put(START_TIME_KEY, Instant.now());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Instant startTime = (Instant) context.get(START_TIME_KEY);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);

        JobKey jobKey = context.getJobDetail()
                               .getKey();

        histogram.record(duration.toMillis(), Attributes.of(AttributeKey.stringKey("job_name"), jobKey.getName(),
                AttributeKey.stringKey("job_group"), jobKey.getGroup()));
    }
}
