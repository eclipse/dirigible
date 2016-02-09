/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

/**
 * The interface which all the runtime services has to implement in order to be registered in the runtime services
 * catalog
 */
public interface IRuntimeServiceDescriptor {

	/**
	 * The name of the service
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * The short description of the service
	 *
	 * @return the description
	 */
	public String getDescription();

	/**
	 * The endpoint URI
	 *
	 * @return the endpoint
	 */
	public String getEndpoint();

	/**
	 * The documentation URL
	 *
	 * @return the documentation
	 */
	public String getDocumentation();

}
