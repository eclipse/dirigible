package org.eclipse.dirigible.core.scheduler.api;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

public abstract class AbstractSynchronizer implements ISynchronizer {

	@Inject
	private IRepository repository;

	protected IRepository getRepository() {
		return repository;
	}

	protected void synchronizeRegistry() throws SynchronizationException {
		ICollection collection = getRepository().getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		if (collection.exists()) {
			synchronizeCollection(collection);
		}
	}

	protected void synchronizeCollection(ICollection collection) throws SynchronizationException {
		List<IResource> resources = collection.getResources();
		for (IResource resource : resources) {
			synchronizeResource(resource);
		}
		List<ICollection> collections = collection.getCollections();
		for (ICollection childCollection : collections) {
			synchronizeCollection(childCollection);
		}
	}

	protected String getRegistryPath(IResource resource) throws SynchronizationException {
		String resourcePath = resource.getPath();
		if (resourcePath.startsWith(IRepositoryStructure.PATH_REGISTRY_PUBLIC)) {
			return resourcePath.substring(IRepositoryStructure.PATH_REGISTRY_PUBLIC.length());
		}
		return resourcePath;
	}

	protected abstract void synchronizeResource(IResource resource) throws SynchronizationException;

	protected abstract void cleanup() throws SynchronizationException;

}
