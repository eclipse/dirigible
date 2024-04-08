package org.eclipse.dirigible;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
public class GCController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCController.class);

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    @GetMapping("/test/gc")
    public void testGC() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(this::runGC, 50, 50, TimeUnit.MILLISECONDS);
    }

    private void runGC() {
        LOGGER.info("--> Before GC call");
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        LOGGER.info("<-- After GC call");
    }
}
