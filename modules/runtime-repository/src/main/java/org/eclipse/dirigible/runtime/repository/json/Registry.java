/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.repository.json;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Registry.
 */
public class Registry extends Collection {

	/** The Constant TYPE_REGISTRY. */
	private static final String TYPE_REGISTRY = "registry";

	/** The name. */
	private String name;

	/** The path. */
	private String path;

	/** The type. */
	private static String type = TYPE_REGISTRY;

	/** The collections. */
	private List<Collection> collections = new ArrayList<Collection>();

	/** The resources. */
	private List<Resource> resources = new ArrayList<Resource>();

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#setPath(java.lang.String)
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#getCollections()
	 */
	@Override
	public List<Collection> getCollections() {
		return collections;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#setCollections(java.util.List)
	 */
	@Override
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#getResources()
	 */
	@Override
	public List<Resource> getResources() {
		return resources;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#setResources(java.util.List)
	 */
	@Override
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.repository.json.Collection#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

}
