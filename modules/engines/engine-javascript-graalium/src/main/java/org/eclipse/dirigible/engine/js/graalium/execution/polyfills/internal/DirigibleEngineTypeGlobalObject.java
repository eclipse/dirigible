package org.eclipse.dirigible.engine.js.graalium.execution.polyfills.internal;

import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.JSGlobalObject;

public class DirigibleEngineTypeGlobalObject implements JSGlobalObject {
    @Override
    public String getName() {
        return "__engine";
    }

    @Override
    public Object getValue() {
        return "graalium";
    }
}
