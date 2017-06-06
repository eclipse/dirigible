package org.eclipse.dirigible.engine.web.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Processing the incoming requests for the raw web content.
 * It supports only GET requests
 *
 */
public class WebEngineProcessor {
	
	@Inject
	private IRepository repository;
	
	/**
	 * 
	 * @param path the requested resource location
	 * @return the {@link IResource} instance
	 */
	public IResource getResource(String path) {
		StringBuilder registryPath = new StringBuilder(IRepositoryStructure.REGISTRY_PUBLIC)
				.append(IRepositoryStructure.SEPARATOR)
				.append(path);
		return repository.getResource(registryPath.toString());
	}

}
