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

public interface ICommonConstants {
	
	public static final String DIRIGIBLE_PRODUCT_NAME = "Eclipse Dirigible"; //$NON-NLS-1$
	public static final String DIRIGIBLE_PRODUCT_VERSION = "2.0.150424"; //$NON-NLS-1$
	
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final String SEPARATOR = "/"; //$NON-NLS-1$
	public static final String DEBUG_SEPARATOR = ":"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$
	
	public static final String SANDBOX = "sandbox"; //$NON-NLS-1$
	public static final String REGISTRY = "registry"; //$NON-NLS-1$
	public static final String WORKSPACE = "workspace"; //$NON-NLS-1$

	public interface ARTIFACT_EXTENSION {
		public static final String JAVASCRIPT= ".js"; //$NON-NLS-1$
		public static final String RUBY = ".rb"; //$NON-NLS-1$
		public static final String GROOVY = ".groovy"; //$NON-NLS-1$
		public static final String JAVA = ".java"; //$NON-NLS-1$
		public static final String COMMAND = ".command"; //$NON-NLS-1$
		public static final String EXTENSION_POINT = ".extensionpoint"; //$NON-NLS-1$
		public static final String EXTENSION = ".extension"; //$NON-NLS-1$
		public static final String SECURITY = ".access"; //$NON-NLS-1$
		public static final String FLOW = ".flow"; //$NON-NLS-1$
		public static final String JOB = ".job"; //$NON-NLS-1$
	}
	
	public interface ARTIFACT_TYPE {
		public final static String DATA_STRUCTURES = "DataStructures"; //$NON-NLS-1$
		public final static String INTEGRATION_SERVICES = "IntegrationServices"; //$NON-NLS-1$
		public final static String SCRIPTING_SERVICES = "ScriptingServices"; //$NON-NLS-1$
		public final static String TEST_CASES = "TestCases"; //$NON-NLS-1$
		public final static String WEB_CONTENT = "WebContent"; //$NON-NLS-1$
		public final static String SECURITY_CONSTRAINTS = "SecurityConstraints"; //$NON-NLS-1$
		public final static String WIKI_CONTENT = "WikiContent"; //$NON-NLS-1$
		public final static String EXTENSION_DEFINITIONS = "ExtensionDefinitions"; //$NON-NLS-1$
		public static final String CONFIGURATION_SETTINGS = "ConfigurationSettings";
	}
	
	public interface ENGINE_TYPE {
		public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
		public static final String JAVA = "java"; //$NON-NLS-1$
		public static final String GROOVY = "groovy"; //$NON-NLS-1$
		public static final String COMMAND = "command"; //$NON-NLS-1$
		public static final String CONDITION = "condition"; //$NON-NLS-1$
		public static final String FLOW = "flow"; //$NON-NLS-1$
		public static final String OUTPUT = "output"; //$NON-NLS-1$
		public static final String JOB = "job"; //$NON-NLS-1$
		public static final String WEB = "web"; //$NON-NLS-1$
		public static final String WIKI = "wiki"; //$NON-NLS-1$
	}
	
	public interface ENGINE_ALIAS {
		public static final String JAVASCRIPT = "js"; //$NON-NLS-1$
		public static final String JAVA = "java"; //$NON-NLS-1$
		public static final String GROOVY = "groovy"; //$NON-NLS-1$
		public static final String COMMAND = "command"; //$NON-NLS-1$
		public static final String FLOW = "flow"; //$NON-NLS-1$
		public static final String JOB = "job"; //$NON-NLS-1$
		public static final String WEB = "web"; //$NON-NLS-1$
		public static final String WIKI = "wiki"; //$NON-NLS-1$
	}

	public static final String DATA_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
	
	public static final String WEB_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;

	public static final String WIKI_CONTENT_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;

	public static final String SCRIPTING_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

	public static final String INTEGRATION_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;

	public static final String SECURITY_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;

	public static final String TESTS_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.TEST_CASES;

	public static final String EXTENSION_REGISTRY_PUBLISH_LOCATION = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC
			+ ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;

}
