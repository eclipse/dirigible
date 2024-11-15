package org.eclipse.dirigible.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/telemetry")
@RestController
public class TelemetryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryController.class);

    private final LongCounter otelMetricCouter;

    TelemetryController(OpenTelemetry openTelemetry) {
        Meter meter = openTelemetry.meterBuilder("my-custom-metrics")
                                   .setInstrumentationVersion("1.0.0")
                                   .build();
        otelMetricCouter = meter.counterBuilder("otel-metric-counter")
                                .setDescription("Custom otel counter")
                                .setUnit("1")
                                .build();
    }

    @GetMapping("/logs")
    ResponseEntity<String> logs() {
        LOGGER.info("Executing /logs");
        return ResponseEntity.ok("/logs: done");
    }

    @GetMapping("/otel-metric")
    ResponseEntity<String> meter() {
        LOGGER.info("Executing /otel-metric");
        AttributeKey<String> attributeKey = AttributeKey.stringKey("my.custom.attribute");
        Attributes attributes = Attributes.of(attributeKey, "Some value");
        otelMetricCouter.add(1, attributes);

        return ResponseEntity.ok("/otel-metric: done");
    }
}
