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

package org.eclipse.dirigible.repository.api;

public interface IRepositoryPaths {

	public static final String SEPARATOR = ICommonConstants.SEPARATOR; //$NON-NLS-1$
	
	public static final String DB_DIRIGIBLE_ROOT = "/db/dirigible/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_REGISTRY = DB_DIRIGIBLE_ROOT + "registry/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_REGISTRY_PUBLIC = DB_DIRIGIBLE_REGISTRY + "public/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_SANDBOX = DB_DIRIGIBLE_ROOT + "sandbox/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_USERS = DB_DIRIGIBLE_ROOT + "users/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_TEMPLATES = DB_DIRIGIBLE_ROOT + "templates/"; //$NON-NLS-1$
	public static final String DB_DIRIGIBLE_TEMPLATES_PROJECTS = DB_DIRIGIBLE_TEMPLATES + "projects/"; //$NON-NLS-1$
	
	public static final String CONF_FOLDER_NAME = "conf"; //$NON-NLS-1$
	public static final String WORKSPACE_FOLDER_NAME = "workspace"; //$NON-NLS-1$
	
	public static final String CONF_REGISTRY = DB_DIRIGIBLE_REGISTRY + CONF_FOLDER_NAME;  

	public static final String REGISTRY_DEPLOY_PATH = DB_DIRIGIBLE_ROOT + "registry/public"; //$NON-NLS-1$
	public static final String REGISTRY_IMPORT_PATH = DB_DIRIGIBLE_ROOT + "registry"; //$NON-NLS-1$
	public static final String SANDBOX_DEPLOY_PATH = DB_DIRIGIBLE_ROOT + "sandbox"; //$NON-NLS-1$


}
