package org.eclipse.dirigible.telemetry;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/telemetry")
@RestController
public class TelemetryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryController.class);

    private final LongCounter otelMetricCouter;
    private final MeterRegistry micrometerMeterRegistry;
    private final APIClient apiClient;

    TelemetryController(OpenTelemetry openTelemetry, MeterRegistry micrometerMeterRegistry, APIClient apiClient) {
        this.micrometerMeterRegistry = micrometerMeterRegistry;
        this.apiClient = apiClient;

        Meter meter = openTelemetry.meterBuilder("dirigible")
                                   .setInstrumentationVersion("1.0.0")
                                   .build();
        otelMetricCouter = meter.counterBuilder("otel_calls_counter")
                                .setDescription("Custom otel counter meter")
                                .setUnit("1")
                                .build();
    }

    @GetMapping("/logs")
    ResponseEntity<String> logs() {
        LOGGER.info("Executing /logs");
        return ResponseEntity.ok("/logs: done");
    }

    @GetMapping("/otel-metric-counter")
    ResponseEntity<String> meter() {
        LOGGER.info("Executing /otel-metric-counter");
        AttributeKey<String> attributeKey = AttributeKey.stringKey("my.custom.attribute");
        Attributes attributes = Attributes.of(attributeKey, "Some value");
        otelMetricCouter.add(1, attributes);

        return ResponseEntity.ok("/otel-metric-counter: done");
    }

    @GetMapping("/micrometer-counter")
    ResponseEntity<String> micrometerCounter() {
        LOGGER.info("Executing /micrometer-counter");
        micrometerMeterRegistry.counter("micrometer_calls_counter")
                               .increment();

        return ResponseEntity.ok("/micrometer-counter: done");
    }

    @WithSpan("my_custom_span")
    @GetMapping("/otel-span")
    ResponseEntity<String> span(@RequestParam("data") String data) {
        LOGGER.info("Executing /otel-span");

        apiClient.makeCall("param1");

        return ResponseEntity.ok("/otel-span: done");
    }

}
