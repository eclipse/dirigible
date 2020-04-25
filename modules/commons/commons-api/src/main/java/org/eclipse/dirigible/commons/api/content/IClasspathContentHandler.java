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
package org.eclipse.dirigible.commons.api.content;

import java.util.Set;

/**
 * The IClasspathContentHandler is the interface for all the classes used to locate and enumerate specific resources in
 * the class path.
 */
public interface IClasspathContentHandler {

	/**
	 * Accept.
	 *
	 * @param path
	 *            the path
	 */
	public void accept(String path);

	/**
	 * Gets the paths.
	 *
	 * @return the paths
	 */
	public Set<String> getPaths();

}
