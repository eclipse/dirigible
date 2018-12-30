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
package org.eclipse.dirigible.runtime.ide.generation.processor;

/**
 * Supported built-in parameters for generation
 *
 */
public interface GenerationParameters {
	
	/** The name of the selected workspace */
	public static final String PARAMETER_WORKSPACE_NAME = "workspaceName";
	
	/** The name of the selected project */
	public static final String PARAMETER_PROJECT_NAME = "projectName";
	
	/** The name of the entered file name */
	public static final String PARAMETER_FILE_NAME = "fileName";
	
	/** The name of the entered file name's extension */
	public static final String PARAMETER_FILE_NAME_EXT = "fileNameExt";
	
	/** The name of the entered file name without extension */
	public static final String PARAMETER_FILE_NAME_BASE = "fileNameBase";
	
	/** The name of the entered file's path */
	public static final String PARAMETER_FILE_PATH = "filePath";
	
	/** The name of the selected package's path */
	public static final String PARAMETER_PACKAGE_PATH = "packagePath";
	
}
