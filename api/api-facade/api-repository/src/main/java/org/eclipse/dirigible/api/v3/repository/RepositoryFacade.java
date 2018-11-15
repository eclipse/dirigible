package org.eclipse.dirigible.api.v3.repository;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.json.Repository;
import org.eclipse.dirigible.runtime.repository.json.RepositoryJsonHelper;
import org.eclipse.dirigible.runtime.repository.processor.RepositoryProcessor;

/**
 * The Repository Facade
 */
public class RepositoryFacade {
	
	private static RepositoryProcessor repositoryProcessor = StaticInjector.getInjector().getInstance(RepositoryProcessor.class);
	
	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	public static IResource getResource(String path) {
		return repositoryProcessor.getResource(path);
	}

	/**
	 * Creates the resource.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param contentType
	 *            the content type
	 * @return the i resource
	 */
	public static IResource createResource(String path, String content, String contentType) {
		return repositoryProcessor.createResource(path, content.getBytes(), contentType);
	}

	/**
	 * Update resource.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @return the i resource
	 */
	public static IResource updateResource(String path, String content) {
		return repositoryProcessor.updateResource(path, content.getBytes());
	}

	/**
	 * Deletes a resource.
	 *
	 * @param path
	 *            the path
	 */
	public static void deleteResource(String path) {
		repositoryProcessor.deleteResource(path);
	}

	/**
	 * Gets the collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public static ICollection getCollection(String path) {
		return repositoryProcessor.getCollection(path);
	}

	/**
	 * Render repository.
	 *
	 * @param collection
	 *            the collection
	 * @return the repository
	 */
	public static Repository renderRepository(ICollection collection) {
		return RepositoryJsonHelper.traverseRepository(collection, "", "");
	}

	/**
	 * Creates a new collection.
	 *
	 * @param path
	 *            the path
	 * @return the collection
	 */
	public static ICollection createCollection(String path) {
		return repositoryProcessor.createCollection(path);
	}

	/**
	 * Deletes a collection.
	 *
	 * @param path
	 *            the path
	 */
	public static void deleteCollection(String path) {
		repositoryProcessor.deleteCollection(path);
	}
}
