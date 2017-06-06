package org.eclipse.dirigible.runtime.registry.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;

/**
 * Processing the Registry Service incoming requests 
 *
 */
public class RegistryProcessor {
	
	private static final String REGISTRY = "/registry";
	@Inject
	private IRepository repository;
	
	public IResource getResource(String path) {
		StringBuilder registryPath = generateRegistryPath(path);
		return repository.getResource(registryPath.toString());
	}
	
	public ICollection getCollection(String path) {
		StringBuilder registryPath = generateRegistryPath(path);
		return repository.getCollection(registryPath.toString());
	}

	public String renderTree(ICollection collection) {
		return RepositoryJsonHelper.collectionToJsonTree(collection, IRepositoryStructure.REGISTRY_PUBLIC, REGISTRY);
	}
	
	private StringBuilder generateRegistryPath(String path) {
		StringBuilder registryPath = new StringBuilder(IRepositoryStructure.REGISTRY_PUBLIC)
				.append(IRepositoryStructure.SEPARATOR)
				.append(path);
		return registryPath;
	}

}
