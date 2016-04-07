/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RepositoryActivator implements BundleActivator {

	private static BundleContext context;

	public static String DIRIGIBLE_PRODUCT_VERSION;

	@Override
	public void start(BundleContext context) throws Exception {
		RepositoryActivator.context = context;
		DIRIGIBLE_PRODUCT_VERSION = context.getBundle().getVersion().toString();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getContext() {
		return context;
	}

}
