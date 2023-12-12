package org.eclipse.dirigible.components.engine.cms;

import java.io.InputStream;

public interface CmisContentStream {
    /**
     * Returns the InputStream of this ContentStream object.
     *
     * @return Input Stream
     */
    InputStream getStream();

    /**
     * Gets the cmis session.
     *
     * @return the cmis session
     */
    CmisSession getCmisSession();

    /**
     * Sets the cmis session.
     *
     * @param cmisSession the new cmis session
     */
    void setCmisSession(CmisSession cmisSession);

    /**
     * Gets the filename.
     *
     * @return the filename
     */
    String getFilename();

    /**
     * Gets the length.
     *
     * @return the length
     */
    long getLength();

    /**
     * Gets the mime type.
     *
     * @return the mime type
     */
    String getMimeType();

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    InputStream getInputStream();
}
