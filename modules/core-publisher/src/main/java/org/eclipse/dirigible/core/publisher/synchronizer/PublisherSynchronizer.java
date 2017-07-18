package org.eclipse.dirigible.core.publisher.synchronizer;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublishCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PublisherSynchronizer extends AbstractSynchronizer {
	
	private static final Logger logger = LoggerFactory.getLogger(PublisherSynchronizer.class);
	
	@Inject
	private PublishCoreService publishCoreService;
	
	private Map<String, String> resourceLocations = new HashMap<String, String>();
	
	private String currentWorkspace = null;
	private String currentRegistry = null;
	private Timestamp currentRequestTime = new Timestamp(0);
	
	@Override
	public void synchronize() {
		synchronized(PublisherSynchronizer.class) {
			logger.trace("Publishing...");
			try {
				List<PublishRequestDefinition> publishRequestDefinitions = getPendingPublishedRequests();
				
				if (publishRequestDefinitions.isEmpty()) {
					logger.trace("Nothing to publish.");
					return;
				}
				
				enumerateResourcesForPublish(publishRequestDefinitions);
				
				synchronizeRegistry();
				
				removeProcessedRequests(publishRequestDefinitions);
				
				cleanup();
			} catch (Exception e) {
				logger.error("Publishing failed.", e);
			}
			logger.trace("Done publishing.");
		}
	}

	
	private void enumerateResourcesForPublish(List<PublishRequestDefinition> publishRequestDefinitions)
			throws SynchronizationException {
		resourceLocations.clear();
		for (PublishRequestDefinition publishRequestDefinition : publishRequestDefinitions) {
			currentWorkspace = publishRequestDefinition.getWorkspace();
			String path = publishRequestDefinition.getPath();
			currentRegistry = (publishRequestDefinition.getRegistry() != null ? publishRequestDefinition.getRegistry() : IRepositoryStructure.REGISTRY_PUBLIC);
			currentRequestTime = (publishRequestDefinition.getCreatedAt().after(currentRequestTime) ? publishRequestDefinition.getCreatedAt() : currentRequestTime);
			
			String sourceLocation = new RepositoryPath(currentWorkspace, path).toString();
			ICollection collection = getRepository().getCollection(sourceLocation);
			if (collection.exists()) {
				synchronizeCollection(collection);
			} else {
				IResource resource = getRepository().getResource(sourceLocation);
				if (resource.exists()) {
					synchronizeResource(resource);
				}
			}
		}
	}

	private List<PublishRequestDefinition> getPendingPublishedRequests() throws PublisherException {
		Timestamp timestamp = publishCoreService.getLatestPublishLog();
		List<PublishRequestDefinition> publishRequestDefinitions = publishCoreService.getPublishRequestsAfter(timestamp);
		return publishRequestDefinitions;
	}

	@Override
	public void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing published artefacts in Registry...");
		
		publishResources();
	
		logger.trace("Done synchronizing published artefacts in Registry.");
	}

	private void publishResources() throws SynchronizationException {
		for (Map.Entry<String, String> entry : resourceLocations.entrySet()) {
			
			// pre publish handlers
			
			
			// publish
			publishResource(entry);
			
			
			
			// post publish handlers
		}
	}

	private void publishResource(Map.Entry<String, String> entry) throws SynchronizationException {
		String sourceLocation = entry.getKey();
		String targetLocation = entry.getValue();
		IResource sourceResource = getRepository().getResource(sourceLocation);
		IResource targetResource = getRepository().getResource(targetLocation);
		if (targetResource.exists()) {
			java.util.Date lastModified = targetResource.getInformation().getModifiedAt();
			if (currentRequestTime.getTime() > lastModified.getTime()) {
				targetResource.setContent(sourceResource.getContent());
			}
		} else {
			getRepository().createResource(targetLocation, sourceResource.getContent());
		}
		try {
			publishCoreService.createPublishLog(sourceResource.getPath(), targetResource.getPath());
		} catch (PublisherException e) {
			throw new SynchronizationException(e);
		}
	}
	
	private void removeProcessedRequests(List<PublishRequestDefinition> publishRequestDefinitions) throws PublisherException {
		for (PublishRequestDefinition publishRequestDefinition : publishRequestDefinitions) {
			publishCoreService.removePublishRequest(publishRequestDefinition.getId());
		}
	}

	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String sourceLocation = resource.getPath();
		String path = sourceLocation.substring(currentWorkspace.length());
		String targetLocation = new RepositoryPath(currentRegistry, path).toString();
		resourceLocations.put(sourceLocation, targetLocation);
	}
	
	@Override
	public void cleanup() throws SynchronizationException {
		resourceLocations.clear();
	}
}
