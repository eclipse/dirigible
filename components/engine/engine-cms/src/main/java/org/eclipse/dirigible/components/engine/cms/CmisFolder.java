package org.eclipse.dirigible.components.engine.cms;

public interface CmisFolder {
    /**
     * Returns true if this CmisInternalFolder is a root folder and false otherwise.
     *
     * @return whether it is a root folder
     */
    boolean isRootFolder();

    /**
     * Returns the Path of this CmisInternalFolder.
     *
     * @return the path
     */
    String getPath();
}
