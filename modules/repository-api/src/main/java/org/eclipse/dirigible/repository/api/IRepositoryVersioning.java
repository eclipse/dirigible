package org.eclipse.dirigible.repository.api;

import java.io.IOException;
import java.util.List;

public interface IRepositoryVersioning {

	/**
	 * Retrieve all the kept versions of a given resource
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<IResourceVersion> getResourceVersions(String path) throws RepositoryVersioningException;

	/**
	 * Retrieve a particular version of a given resource
	 *
	 * @param path
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public IResourceVersion getResourceVersion(String path, int version) throws RepositoryVersioningException;

}
