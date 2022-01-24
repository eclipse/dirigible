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
package org.eclipse.dirigible.commons.api.content;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

/**
 * The AbstractClasspathContentHandler is the parent of the classes used to locate and enumerate specific resources in
 * the class path.
 */
public abstract class AbstractClasspathContentHandler implements IClasspathContentHandler {

	/** The resources. */
	private final Set<String> resources = Collections.synchronizedSet(new HashSet<String>());

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.IClasspathContentHandler#accept(java.lang.String)
	 */
	@Override
	public void accept(String path) {
		if (isValid(path)) {
			resources.add(path);
			getLogger().info("Added: " + path);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.IClasspathContentHandler#getPaths()
	 */
	@Override
	public Set<String> getPaths() {
		return resources;
	}

	/**
	 * Checks if is valid.
	 *
	 * @param path
	 *            the path
	 * @return true, if is valid
	 */
	protected abstract boolean isValid(String path);

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	protected abstract Logger getLogger();

}
