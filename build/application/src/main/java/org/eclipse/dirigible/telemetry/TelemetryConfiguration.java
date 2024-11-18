package org.eclipse.dirigible.telemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!open-telemetry")
@Configuration
public class TelemetryConfiguration {

    @Bean
    OpenTelemetry provideOpenTelemetry() {
        return GlobalOpenTelemetry.get();
    }

}
