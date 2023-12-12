package org.eclipse.dirigible.components.engine.cms;

import java.io.IOException;

public interface CmisObject {

    /**
     * Sanitize.
     *
     * @param path the path
     * @return the string
     */
    String sanitize(String path);

    /**
     * Returns the ID of this CmisObject.
     *
     * @return the Id
     */
    String getId();

    /**
     * Returns the Name of this CmisObject.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the Type of this CmisObject.
     *
     * @return the object type
     */
    ObjectType getType();

    /**
     * Delete this CmisObject.
     *
     * @throws IOException IO Exception
     */
    void delete() throws IOException;

    /**
     * Delete this CmisObject.
     *
     * @param allVersions whether to delete all versions
     * @throws IOException IO Exception
     */
    void delete(boolean allVersions) throws IOException;

    /**
     * Rename this CmisObject.
     *
     * @param newName the new name
     * @throws IOException IO Exception
     */
    void rename(String newName) throws IOException;
}
