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

package org.eclipse.dirigible.cli.utils;

public interface CommonProperties {

	public interface CLI {
		public static final String COMMAND_IMPORT = "import";
		public static final String COMMAND = "command";
	}

	public interface RemoteCommand {
		public static final String PROPERTY_PROXY_HOST = "proxyHost";
		public static final String PROPERTY_PROXY_PORT = "proxyPort";
		public static final String PROPERTY_PROXY_SCHEME = "proxyScheme";
	}

	public interface ImportProjectCommand extends RemoteCommand {
		public static final String PROPERTY_URL = "url";
		public static final String PROPERTY_ARCHIVE = "archive";
		public static final String PROPERTY_OVERRIDE = "override";
	}
}
