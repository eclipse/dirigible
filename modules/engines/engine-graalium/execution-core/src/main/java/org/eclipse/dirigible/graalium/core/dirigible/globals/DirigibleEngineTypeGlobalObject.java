package org.eclipse.dirigible.graalium.core.dirigible.globals;

import org.eclipse.dirigible.graalium.core.graal.globals.JSGlobalObject;

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
