package org.eclipse.dirigible.telemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenTelemetryConfig {

    public static final String OTEL_JAVA_GLOBAL_AUTOCONFIGURE_ENABLED = "otel.java.global-autoconfigure.enabled";

    @Bean
    OpenTelemetry provideOpenTelemetry(@Value("${" + OTEL_JAVA_GLOBAL_AUTOCONFIGURE_ENABLED + ":false}") String javaAutoconfigureEnabled) {
        boolean enabled = Boolean.parseBoolean(javaAutoconfigureEnabled);

        // explicitly set system property for this based in spring config
        System.setProperty(OTEL_JAVA_GLOBAL_AUTOCONFIGURE_ENABLED, String.valueOf(enabled));

        return GlobalOpenTelemetry.get();
    }
}
