package org.eclipse.dirigible.components.jobs;

import java.util.Optional;

public abstract class SystemJob extends DirigibleJob {

    @Override
    protected final Optional<String> getTriggerGroup() {
        return Optional.of("system");
    }

    @Override
    protected final Optional<String> getJobGroup() {
        return Optional.of("system");
    }
}
