package org.eclipse.dirigible.components.jobs.domain;

public enum JobStatus {
    // values are used in DB and in the UI as well
    // change them with caution
    TRIGGRED, FINISHED, FAILED, LOGGED, UNKNOWN, INFO, WARN, ERROR
}
