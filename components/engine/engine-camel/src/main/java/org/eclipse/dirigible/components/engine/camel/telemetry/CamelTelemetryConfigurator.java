package org.eclipse.dirigible.components.engine.camel.telemetry;

import org.apache.camel.CamelContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
class CamelTelemetryConfigurator implements ApplicationListener<ApplicationReadyEvent> {

    private final CamelContext camelContext;

    CamelTelemetryConfigurator(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // OpenTelemetryTracer otelTracer = new OpenTelemetryTracer();
        // otelTracer.init(camelContext);
    }
}
