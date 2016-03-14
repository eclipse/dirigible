/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.integration.publish;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public interface IntegrationConstants {

	public static final String IS_CONTENT_FOLDER = ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	public static final String IS_REGISTYRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + IS_CONTENT_FOLDER;

	// public static final String EXTENSION_ROUTE = ".routes"; //$NON-NLS-1$
	// public static final String EXTENSION_WS = ".ws"; //$NON-NLS-1$
	// public static final String EXTENSION_XSL = ".xsl"; //$NON-NLS-1$
	// public static final String EXTENSION_XSLT = ".xslt"; //$NON-NLS-1$
	//
	// // Actions
	// public static final String ACTION = "ACTION"; //$NON-NLS-1$
	// public static final String ACTION_LOAD = "ACTION_LOAD"; //$NON-NLS-1$
	// public static final String ACTION_PING = "ACTION_PING"; //$NON-NLS-1$
	// public static final String ACTION_ROUTES = "ACTION_ROUTES"; //$NON-NLS-1$
	// public static final String ACTION_WS = "ACTION_WS"; //$NON-NLS-1$
	//
	// // Statuses
	// public static final String STATUS = "STATUS"; //$NON-NLS-1$
	// public static final String STATUS_OK = "STATUS_OK"; //$NON-NLS-1$
	// public static final String STATUS_FAILED = "STATUS_FAILED"; //$NON-NLS-1$

}
