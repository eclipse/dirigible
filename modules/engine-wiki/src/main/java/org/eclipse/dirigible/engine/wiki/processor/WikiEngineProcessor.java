package org.eclipse.dirigible.engine.wiki.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Processing the incoming requests for the wiki pages.
 * It supports only GET requests
 */
public class WikiEngineProcessor {

	@Inject
	private WikiEngineExecutor wikiEngineExecutor;

	/**
	 * @param path
	 *            the requested resource location
	 * @return if the {@link IResource}
	 */
	public boolean existResource(String path) {
		return wikiEngineExecutor.existResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

	/**
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} instance
	 */
	public IResource getResource(String path) {
		return wikiEngineExecutor.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

	/**
	 * @param path
	 *            the requested resource location
	 * @return the {@link IResource} content as a byte array
	 */
	public byte[] getResourceContent(String path) {
		return wikiEngineExecutor.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}

}
