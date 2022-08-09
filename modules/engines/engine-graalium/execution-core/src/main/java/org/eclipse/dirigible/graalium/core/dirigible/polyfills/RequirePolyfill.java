package org.eclipse.dirigible.graalium.core.dirigible.polyfills;

import org.eclipse.dirigible.graalium.core.graal.polyfills.JavascriptPolyfill;

/**
 * The Class RequirePolyfill.
 */
public class RequirePolyfill implements JavascriptPolyfill {
    
    /** The Constant POLYFILL_PATH_IN_RESOURCES. */
    private static final String POLYFILL_PATH_IN_RESOURCES = "/polyfills/require.js";

    /**
     * Instantiates a new require polyfill.
     */
    public RequirePolyfill() {
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return this.getPolyfillFromResources("/polyfills/require.js");
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return "/polyfills/require.js";
    }
}

