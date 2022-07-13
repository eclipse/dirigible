package org.eclipse.dirigible.graalium.core.graal.modules;

import java.nio.file.Path;

public interface ModuleResolver {

    boolean isResolvable(String moduleToResolve);

    Path resolve(String moduleToResolve);
}
