package org.eclipse.dirigible.engine.js.graalvm.execution.js.platform;

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.dirigible.commons.config.Configuration;
import org.graalvm.polyglot.Engine;

public class GraalJSEngineCreator {
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN = "DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN";

    private static Engine engine = null;
    private static Engine debuggableEngine = null;

    public static Engine getOrCreateEngine() {
        if (engine == null) {
            engine = getDefaultEngineBuilder().build();
        }

        return engine;
    }

    public static Engine getOrCreateDebuggableEngine() {
        if (debuggableEngine == null) {
            debuggableEngine = getDefaultEngineBuilder()
                    .option("inspect", "9229")
                    .option("inspect.Secure", "false")
                    .option("inspect.Path", "test")
                    .build();
        }

        return debuggableEngine;
    }

    private static Engine.Builder getDefaultEngineBuilder() {
        Engine.Builder engineBuilder = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.esm-eval-returns-exports", "true");

        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN, "true"))) {
            engineBuilder.option("js.nashorn-compat", "true");
        }

        return engineBuilder;
    }
}
