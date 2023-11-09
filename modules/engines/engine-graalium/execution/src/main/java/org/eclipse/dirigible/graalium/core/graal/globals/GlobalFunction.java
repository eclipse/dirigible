package org.eclipse.dirigible.graalium.core.graal.globals;

import org.graalvm.polyglot.proxy.ProxyExecutable;

public interface GlobalFunction extends ProxyExecutable {
    String getName();
}
