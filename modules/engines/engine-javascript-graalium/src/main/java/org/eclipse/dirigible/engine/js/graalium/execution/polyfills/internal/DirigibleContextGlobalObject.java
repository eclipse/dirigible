package org.eclipse.dirigible.engine.js.graalium.execution.polyfills.internal;

import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.JSGlobalObject;

import java.util.HashMap;
import java.util.Map;

public class DirigibleContextGlobalObject implements JSGlobalObject {

    private final Map<Object, Object> dirigibleContextValue;

    public DirigibleContextGlobalObject(Map<Object, Object> dirigibleContextValue) {
        this.dirigibleContextValue = dirigibleContextValue != null ? dirigibleContextValue : new HashMap<>();
    }

    @Override
    public String getName() {
        return "__context";
    }

    @Override
    public Object getValue() {
        return dirigibleContextValue;
    }
}
