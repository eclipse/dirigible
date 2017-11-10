/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api.resource;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;

public interface IResourceExecutor {
	
	public byte[] getResourceContent(String root, String module) throws RepositoryException;
	
	public byte[] getResourceContent(String root, String module, String extension) throws RepositoryException;
	
	public ICollection getCollection(String root, String module) throws RepositoryException;

	public IResource getResource(String root, String module) throws RepositoryException;
	
	public IResource getResource(String root, String module, String extension) throws RepositoryException;
	
	public boolean existResource(String root, String module) throws RepositoryException;
	
	public boolean existResource(String root, String module, String extension) throws RepositoryException;
	
	public String createResourcePath(String root, String module);
	
	public String createResourcePath(String root, String module, String extension);

}
