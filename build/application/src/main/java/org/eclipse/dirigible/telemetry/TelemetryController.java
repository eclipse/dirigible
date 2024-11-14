package org.eclipse.dirigible.telemetry;

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

    @GetMapping("/logs")
    ResponseEntity<String> logs() {
        LOGGER.info("Some test log in telemetry endpoint");
        return ResponseEntity.ok("Logs: done");
    }
}
