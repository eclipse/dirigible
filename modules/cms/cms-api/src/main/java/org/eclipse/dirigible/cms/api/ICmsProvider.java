/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.api;

/**
 * The Content Management System interface implemented by all the CMS providers
 *
 */
public interface ICmsProvider {

	public static final String DIRIGIBLE_CMS_PROVIDER = "DIRIGIBLE_CMS_PROVIDER"; //$NON-NLS-1$

	public static final String DIRIGIBLE_CMS_PROVIDER_INTERNAL = "internal"; //$NON-NLS-1$

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType();

	/**
	 * Getter for the underlying repository session object
	 * 
	 * @return the session object
	 */
	public Object getSession();

}
