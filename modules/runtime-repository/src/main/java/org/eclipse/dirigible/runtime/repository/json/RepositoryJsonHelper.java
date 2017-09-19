package org.eclipse.dirigible.runtime.repository.json;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;

public class RepositoryJsonHelper {

	public static Repository traverseRepository(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Repository repositoryPojo = new Repository();
		repositoryPojo.setName(collection.getName());
		repositoryPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			repositoryPojo.getCollections().add(traverseCollection(childCollection, removePathPrefix, addPathPrefix));
		}

		for (IResource childResource : collection.getResources()) {
			Resource resourcePojo = new Resource();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			repositoryPojo.getResources().add(resourcePojo);
		}

		return repositoryPojo;
	}

	public static Registry traverseRegistry(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Registry registryPojo = new Registry();
		registryPojo.setName(collection.getName());
		registryPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			registryPojo.getCollections().add(traverseCollection(childCollection, removePathPrefix, addPathPrefix));
		}

		for (IResource childResource : collection.getResources()) {
			Resource resourcePojo = new Resource();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			registryPojo.getResources().add(resourcePojo);
		}

		return registryPojo;
	}

	public static Collection traverseCollection(ICollection collection, String removePathPrefix, String addPathPrefix) {
		Collection collectionPojo = new Collection();
		collectionPojo.setName(collection.getName());
		collectionPojo.setPath(addPathPrefix + collection.getPath().substring(removePathPrefix.length()));
		for (ICollection childCollection : collection.getCollections()) {
			collectionPojo.getCollections().add(traverseCollection(childCollection, removePathPrefix, addPathPrefix));
		}

		for (IResource childResource : collection.getResources()) {
			Resource resourcePojo = new Resource();
			resourcePojo.setName(childResource.getName());
			resourcePojo.setPath(addPathPrefix + childResource.getPath().substring(removePathPrefix.length()));
			resourcePojo.setContentType(childResource.getContentType());
			collectionPojo.getResources().add(resourcePojo);
		}

		return collectionPojo;
	}

}
