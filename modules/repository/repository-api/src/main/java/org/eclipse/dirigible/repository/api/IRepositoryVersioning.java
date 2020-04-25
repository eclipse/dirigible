/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.api;

import java.util.List;

/**
 * The interface containing versioning related methods of the repository .
 */
public interface IRepositoryVersioning {

	/**
	 * Retrieve all the kept versions of a given resource.
	 *
	 * @param path
	 *            the location of the {@link IResource}
	 * @return a list of {@link IResourceVersion} instances
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 */
	public List<IResourceVersion> getResourceVersions(String path) throws RepositoryVersioningException;

	/**
	 * Retrieve a particular version of a given resource.
	 *
	 * @param path
	 *            the location of the {@link IResource}
	 * @param version
	 *            the exact version
	 * @return a {@link IResourceVersion} instance
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 */
	public IResourceVersion getResourceVersion(String path, int version) throws RepositoryVersioningException;

}
