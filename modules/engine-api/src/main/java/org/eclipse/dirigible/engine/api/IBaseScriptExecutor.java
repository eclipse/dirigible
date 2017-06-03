/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.engine.api;

import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;

public interface IBaseScriptExecutor {

	byte[] readResourceData(String repositoryPath) throws RepositoryException;

	Module retrieveModule(String module, String extension, String rootPath) throws RepositoryException;

	List<Module> retrieveModulesByExtension(String extension, String rootPath) throws RepositoryException;

	ICollection getCollection(String repositoryPath) throws RepositoryException;

	IResource getResource( String repositoryPath) throws RepositoryException;

}
