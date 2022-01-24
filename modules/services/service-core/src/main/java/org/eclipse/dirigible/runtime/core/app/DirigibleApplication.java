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
package org.eclipse.dirigible.runtime.core.app;

import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.dirigible.runtime.core.listener.DirigibleServletContextListener;

/**
 * Sets the singletons from in the Application context.
 */
public class DirigibleApplication extends Application {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		return DirigibleServletContextListener.getServices();
	}

}
