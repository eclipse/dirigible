package org.eclipse.dirigible.graalium.core.graal.polyfills;

/**
 * The Class GlobalPolyfill.
 */
public class GlobalPolyfill implements JavascriptPolyfill {
    
    /** The Constant POLYFILL_PATH_IN_RESOURCES. */
    private static final String POLYFILL_PATH_IN_RESOURCES = "/polyfills/global.js";

    /**
     * Gets the source.
     *
     * @return the source
     */
    @Override
    public String getSource() {
        return getPolyfillFromResources(POLYFILL_PATH_IN_RESOURCES);
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    @Override
    public String getFileName() {
        return POLYFILL_PATH_IN_RESOURCES;
    }
}
