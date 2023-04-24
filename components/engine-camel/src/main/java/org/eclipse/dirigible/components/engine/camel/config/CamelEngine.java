package org.eclipse.dirigible.components.engine.camel.config;

import org.eclipse.dirigible.components.base.artefact.Engine;
import org.springframework.stereotype.Component;

@Component
public class CamelEngine implements Engine {
    @Override
    public String getName() {
        return "Process (Camel)";
    }

    @Override
    public String getProvider() {
        return "Eclipse Dirigible";
    }
}
