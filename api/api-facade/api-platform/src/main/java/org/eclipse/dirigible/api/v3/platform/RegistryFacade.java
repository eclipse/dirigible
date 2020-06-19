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
package org.eclipse.dirigible.api.v3.platform;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;

public class RegistryFacade {

	public static byte[] getContent(String path) throws IOException, ScriptingException {
		// Check in the repository first
		IResource resource = RepositoryFacade.getResource(toRepositoryPath(path));
		if (resource.exists()) {
			return resource.getContent();
		}

		// Check in the pre-delivered content
		InputStream in = RegistryFacade.class.getResourceAsStream(toResourcePath(path));
		try {
			if (in != null) {
				return IOUtils.toByteArray(in);
			} else {
				throw new ScriptingException(format("Resource [{0}] does not exist", path));
			} 
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static String getText(String path) throws IOException, ScriptingException {
		return new String(getContent(path));
	}

	private static String toRepositoryPath(String path) {
		return new RepositoryPath().append(IRepositoryStructure.PATH_REGISTRY_PUBLIC).append(path).build();
	}

	private static String toResourcePath(String path) {
		if (!path.startsWith(IRepositoryStructure.SEPARATOR)) {
			return IRepositoryStructure.SEPARATOR + path;
		}
		return path;
	}
}
