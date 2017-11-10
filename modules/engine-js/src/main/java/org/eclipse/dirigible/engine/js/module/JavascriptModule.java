/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.module;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;

public class JavascriptModule extends AbstractDirigibleModule {

	private static final String MODULE_NAME = "Javascript Module";
	
	@Override
	protected void configure() {
		ServiceLoader<IJavascriptEngineExecutor> javascriptEngineExecutors = ServiceLoader.load(IJavascriptEngineExecutor.class);
		
		Configuration.load("/dirigible-js.properties");
		
		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT, IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
		for (IJavascriptEngineExecutor next : javascriptEngineExecutors) {
			if (next.getType().equals(javascriptEngineType)) {
				bind(IJavascriptEngineExecutor.class).toInstance(next);
			}
		}

	}
	
	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
