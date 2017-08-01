package org.eclipse.dirigible.engine.web.processor;

import javax.inject.Inject;

import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

/**
 * Processing the incoming requests for the raw web content.
 * It supports only GET requests
 *
 */
public class WebEngineProcessor {
	
	@Inject
	private WebEngineExecutor webEngineExecutor;
	
	/**
	 * 
	 * @param path the requested resource location
	 * @return if the {@link IResource} 
	 */
	public boolean existResource(String path) {
		return webEngineExecutor.existResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}
	
	/**
	 * 
	 * @param path the requested resource location
	 * @return the {@link IResource} instance
	 */
	public IResource getResource(String path) {
		return webEngineExecutor.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}
	 
	/**
	 * 
	 * @param path the requested resource location
	 * @return the {@link IResource} content as a byte array
	 */
	public byte[] getResourceContent(String path) {
		return webEngineExecutor.getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, path);
	}


}
