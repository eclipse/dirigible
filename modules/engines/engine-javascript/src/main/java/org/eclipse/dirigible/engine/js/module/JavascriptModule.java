/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.module;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;

/**
 * The Javascript Module.
 */
public class JavascriptModule extends AbstractDirigibleModule {

	/** The Constant MODULE_NAME. */
	private static final String MODULE_NAME = "Javascript Module";

	/**
	 * Configure.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#configure()
	 */
	@Override
	public void configure() {
		ServiceLoader<IJavascriptEngineExecutor> javascriptEngineExecutors = ServiceLoader.load(IJavascriptEngineExecutor.class);

		Configuration.loadModuleConfig("/dirigible-js.properties");

		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_DEFAULT,
				IJavascriptEngineExecutor.JAVASCRIPT_TYPE_GRAALIUM);
		for (IJavascriptEngineExecutor next : javascriptEngineExecutors) {
			if (next.getType().equals(javascriptEngineType)) {
				StaticObjects.set(StaticObjects.JAVASCRIPT_ENGINE, next);
			}
		}

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public int getPriority() {
		return PRIORITY_ENGINE;
	}
}
