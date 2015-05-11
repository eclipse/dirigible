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

package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

public class DefinitionEnumerator {

	private final List<String> list = new ArrayList<String>();
	
	public DefinitionEnumerator(final String repositoryPath, final ICollection collection,
			final String fileExtension) throws IOException {
		enumerateJsDefinitions(repositoryPath, collection, fileExtension);
	}

	public List<String> toArrayList() {
		return list;
	}

	private void enumerateJsDefinitions(final String repositoryPath, final ICollection collection,
			final String fileExtension) throws IOException {
		if (collection.exists()) {
			for (final IResource resource : collection.getResources()) {
				if (resource != null && resource.getName() != null) {
					if (resource.getName().endsWith(fileExtension)) {
						String collecationPath = collection.getPath();
						if (collecationPath.length() >= repositoryPath.length()) {
							final String fullPath = collecationPath.substring(repositoryPath.length())
									+ IRepository.SEPARATOR + resource.getName();
							this.list.add(fullPath);
						}
					}
				}
			}
			for (final ICollection subCollection : collection.getCollections()) {
				enumerateJsDefinitions(repositoryPath, subCollection, fileExtension);
			}
		}

	}

}
