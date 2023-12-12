package org.eclipse.dirigible.components.engine.cms;

import java.io.IOException;

public interface CmisSession {
    /**
     * Returns a CMIS Object by name.
     *
     * @param id the Id
     * @return CMIS Object
     * @throws IOException IO Exception
     */
    public CmisObject getObject(String id) throws IOException;

    /**
     * Returns a CMIS Object by path.
     *
     * @param path the path
     * @return CMIS Object
     * @throws IOException IO Exception
     */
    public CmisObject getObjectByPath(String path) throws IOException;
}
