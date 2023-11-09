package org.eclipse.dirigible.graalium.core.javascript.modules;

import org.graalvm.polyglot.Value;

public record Module(Value module, ModuleType moduleType) {
}
