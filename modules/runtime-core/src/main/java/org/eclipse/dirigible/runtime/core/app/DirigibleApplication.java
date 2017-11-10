/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.core.app;

import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.dirigible.runtime.core.listener.DirigibleServletContextListener;

// TODO: Auto-generated Javadoc
/**
 * Sets the singletons from Guice in the Application context.
 */
public class DirigibleApplication extends Application {

	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		return DirigibleServletContextListener.getServices();
	}

}
