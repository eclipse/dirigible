package org.eclipse.dirigible.runtime.repository.json;

import java.util.ArrayList;
import java.util.List;

public class Registry extends Collection {

	private static final String TYPE_REGISTRY = "registry";

	private String name;

	private String path;

	private static String type = TYPE_REGISTRY;

	private List<Collection> collections = new ArrayList<Collection>();

	private List<Resource> resources = new ArrayList<Resource>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public List<Collection> getCollections() {
		return collections;
	}

	@Override
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	@Override
	public List<Resource> getResources() {
		return resources;
	}

	@Override
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	@Override
	public String getType() {
		return type;
	}

}
