package org.eclipse.dirigible.graalium.core.graal;

import org.eclipse.dirigible.graalium.core.graal.configuration.Configuration;
import org.graalvm.polyglot.Engine;

public class GraalJSEngineCreator {
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
                    .option("inspect", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PORT", "9229"))
                    .option("inspect.Secure", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SECURE", "false"))
                    .option("inspect.Suspend", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SUSPEND", "false"))
                    .option("inspect.Path", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PATH", "execution-debug"))
                    .build();
        }

        return debuggableEngine;
    }

    private static Engine.Builder getDefaultEngineBuilder() {
        return Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.esm-eval-returns-exports", "true");
    }
}
