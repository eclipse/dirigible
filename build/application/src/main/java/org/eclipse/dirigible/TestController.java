package org.eclipse.dirigible;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    ResponseEntity<String> test() {
        LOGGER.info("Some log in test endpoint");
        return ResponseEntity.ok("Done");
    }
}
