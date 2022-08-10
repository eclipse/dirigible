package org.eclipse.dirigible.graalium.core.dirigible.globals;

import org.eclipse.dirigible.graalium.core.graal.globals.JSGlobalObject;

/**
 * The Class DirigibleEngineTypeGlobalObject.
 */
public class DirigibleEngineTypeGlobalObject implements JSGlobalObject {
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return "__engine";
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    @Override
    public Object getValue() {
        return "graalium";
    }
}
