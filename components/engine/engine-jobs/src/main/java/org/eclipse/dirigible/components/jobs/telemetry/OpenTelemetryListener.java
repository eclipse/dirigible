package org.eclipse.dirigible.components.jobs.telemetry;

import org.quartz.listeners.JobListenerSupport;

abstract class OpenTelemetryListener extends JobListenerSupport {

    @Override
    public final String getName() {
        return this.getClass()
                   .getSimpleName();
    }

}
