package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills;

public class XhrPolyfill implements JSPolyfill {

    private static final String POLYFILL_PATH_IN_RESOURCES = "/polyfills/xhr.js";

    @Override
    public String getSource() {
        return getPolyfillFromResources(POLYFILL_PATH_IN_RESOURCES);
    }

    @Override
    public String getFileName() {
        return "/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/main/resources/polyfills/xhr.js";
    }
}
