package org.eclipse.dirigible.graalium.core.graal;

import org.eclipse.dirigible.graalium.core.graal.configuration.Configuration;
import org.graalvm.polyglot.Engine;

/**
 * The Class GraalJSEngineCreator.
 */
public class GraalJSEngineCreator {
    
    /** The engine. */
    private static Engine engine = null;
    
    /** The debuggable engine. */
    private static Engine debuggableEngine = null;

    /**
     * Gets the or create engine.
     *
     * @return the or create engine
     */
    public static Engine getOrCreateEngine() {
        if (engine == null) {
            engine = getDefaultEngineBuilder().build();
        }

        return engine;
    }

    /**
     * Gets the or create debuggable engine.
     *
     * @return the or create debuggable engine
     */
    public static Engine getOrCreateDebuggableEngine() {
        if (debuggableEngine == null) {
            debuggableEngine = getDefaultEngineBuilder()
                    .option("inspect", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PORT", "8081"))
                    .option("inspect.Secure", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SECURE", "false"))
                    .option("inspect.Suspend", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SUSPEND", "true"))
                    .option("inspect.Path", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PATH", "debug"))
                    .build();
        }

        return debuggableEngine;
    }

    /**
     * Gets the default engine builder.
     *
     * @return the default engine builder
     */
    private static Engine.Builder getDefaultEngineBuilder() {
        return Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.esm-eval-returns-exports", "true");
    }
}
