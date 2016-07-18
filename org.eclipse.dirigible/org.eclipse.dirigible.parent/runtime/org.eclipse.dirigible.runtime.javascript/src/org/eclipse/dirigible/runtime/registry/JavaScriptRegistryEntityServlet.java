/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

public class JavaScriptRegistryEntityServlet extends JavaScriptRegistryServlet {

	private static final long serialVersionUID = -3444634571601169876L;

	@Override
	protected String getFileExtension() {
		return ".entity";
	}

}
