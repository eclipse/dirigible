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
	
	public IRepository getRepository() {
		return repository;
	}
	
	@Override
	public void synchronizeRegistry() throws SynchronizationException {
		ICollection collection = getRepository().getCollection(IRepositoryStructure.REGISTRY_PUBLIC);
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

	protected abstract void synchronizeResource(IResource resource) throws SynchronizationException;

}
