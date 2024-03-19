package org.eclipse.dirigible.components.jobs.handler;

import org.quartz.JobExecutionException;

public class MissingTenantException extends JobExecutionException {

    public MissingTenantException(String message) {
        super(message);
    }
}
