package org.eclipse.dirigible.components.base.initializer;

/**
 * Base interface for initializers that would be called after all classpath content is expanded on the filesystem
 */
public interface Initializer {
    /**
     * Method that would be called for initialization work to be done
     */
    void initialize();
}
