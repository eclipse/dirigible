/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.content;

import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Interface IClasspathContentHandler.
 */
public interface IClasspathContentHandler {
	
	/**
	 * Accept.
	 *
	 * @param path the path
	 */
	public void accept(String path);
	
	/**
	 * Gets the paths.
	 *
	 * @return the paths
	 */
	public Set<String> getPaths();

}
