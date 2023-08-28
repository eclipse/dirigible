package org.eclipse.dirigible.commons.process.execution;

import javax.annotation.Nullable;

public class ProcessExecutionOptions {
    private String workingDirectory;

    public void setWorkingDirectory(@Nullable String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Nullable
    public String getWorkingDirectory() {
        return workingDirectory;
    }
}
