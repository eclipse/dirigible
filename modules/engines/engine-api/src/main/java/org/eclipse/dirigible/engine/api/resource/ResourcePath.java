/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.api.resource;

/**
 * The Resource Path.
 */
public class ResourcePath {

	private String module;

	private String path;

	/**
	 * Instantiates a new resource path.
	 */
	public ResourcePath() {
	}

	/**
	 * Instantiates a new resource path.
	 *
	 * @param module
	 *            the module
	 * @param path
	 *            the path
	 */
	public ResourcePath(String module, String path) {
		super();
		this.module = module;
		this.path = path;
	}

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * Sets the module.
	 *
	 * @param module
	 *            the new module
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
