package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.pool.LeakedConnectionsDoctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class LeakedConnectionsRemediationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeakedConnectionsRemediationInitializer.class);

    static {
        LeakedConnectionsDoctor.init();
        LOGGER.info("Initialized [{}]...", LeakedConnectionsDoctor.class);
    }
}
