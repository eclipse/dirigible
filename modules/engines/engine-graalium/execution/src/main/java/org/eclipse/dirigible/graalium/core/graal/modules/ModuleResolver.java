package org.eclipse.dirigible.graalium.core.graal.modules;

import java.nio.file.Path;

/**
 * The Interface ModuleResolver.
 */
public interface ModuleResolver {

    /**
     * Checks if is resolvable.
     *
     * @param moduleToResolve the module to resolve
     * @return true, if is resolvable
     */
    boolean isResolvable(String moduleToResolve);

    /**
     * Resolve.
     *
     * @param moduleToResolve the module to resolve
     * @return the path
     */
    Path resolve(String moduleToResolve);
}
