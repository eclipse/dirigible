/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.nashorn;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptExecutor;

public class NashornJavaScriptEngineProvider implements IJavaScriptEngineProvider {

	private static final Logger logger = Logger.getLogger(NashornJavaScriptEngineProvider.class);

	@Override
	public String getType() {
		return "nashorn";
	}

	@Override
	public IJavaScriptEngineExecutor create(IJavaScriptExecutor javaScriptExecutor) {
		return new NashornJavaScriptEngineExecutor(javaScriptExecutor);
	}

}
