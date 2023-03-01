/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.cms;

public interface CmsProvider {
	
	/** The Constant DIRIGIBLE_CMS_PROVIDER. */
	public static final String DIRIGIBLE_CMS_PROVIDER = "DIRIGIBLE_CMS_PROVIDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_PROVIDER_INTERNAL. */
	public static final String DIRIGIBLE_CMS_PROVIDER_INTERNAL = "internal"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_PROVIDER_DATABASE. */
	public static final String DIRIGIBLE_CMS_PROVIDER_DATABASE = "database"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER. */
	public static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE. */
	public static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER_IS_ABSOLUTE"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE. */
	public static final String DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE = "DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE";
	
	/** The Constant DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME. */
	public static final String DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME = "DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME";
	
	/**
	 * Getter for the underlying repository session object.
	 *
	 * @return the session object
	 */
	public Object getSession();

}
