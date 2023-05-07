package org.eclipse.dirigible.components.initializers.synchronizer;

import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronizationWatcherPublisherHandler implements PublisherHandler {
	
	/** The synchronization watcher. */
	@Autowired
	private SynchronizationWatcher synchronizationWatcher;

	@Override
	public void beforePublish(String location) {
		
	}

	@Override
	public void afterPublish(String workspaceLocation, String registryLocation) {
		synchronizationWatcher.force();
	}

	@Override
	public void beforeUnpublish(String location) {
		
	}

	@Override
	public void afterUnpublish(String location) {
		synchronizationWatcher.force();
	}

}
