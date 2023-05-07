package org.eclipse.dirigible.components.base.publisher;

/**
 * The Interface PublisherHandler.
 */
public interface PublisherHandler {
	
	/**
	 * Before publish.
	 *
	 * @param location the location
	 */
    void beforePublish(String location);

    /**
     * After publish.
     *
     * @param workspaceLocation the workspace location
     * @param registryLocation the registry location
     */
    void afterPublish(String workspaceLocation, String registryLocation);

    /**
     * Before unpublish.
     *
     * @param location the location
     */
    void beforeUnpublish(String location);

    /**
     * After unpublish.
     *
     * @param location the location
     */
    void afterUnpublish(String location);

}
