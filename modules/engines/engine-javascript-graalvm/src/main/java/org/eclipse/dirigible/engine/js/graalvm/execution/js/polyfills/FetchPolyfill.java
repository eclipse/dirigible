package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills;

public class FetchPolyfill implements JSPolyfill{
    private static final String POLYFILL_PATH_IN_RESOURCES = "/polyfills/fetch.js";

    @Override
    public String getSource() {
        return getPolyfillFromResources(POLYFILL_PATH_IN_RESOURCES);
    }

    @Override
    public String getFileName() {
        return POLYFILL_PATH_IN_RESOURCES;
    }
}
