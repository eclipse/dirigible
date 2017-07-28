package org.eclipse.dirigible.runtime.repository.json;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;

public class RepositoryJsonHelper {
	
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
