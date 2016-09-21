/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RuntimeActivator implements BundleActivator {

	public static BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		RuntimeActivator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getContext() {
		return context;
	}

}
