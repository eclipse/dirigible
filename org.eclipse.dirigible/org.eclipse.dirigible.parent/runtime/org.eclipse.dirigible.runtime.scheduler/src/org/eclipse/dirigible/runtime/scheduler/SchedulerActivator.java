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

package org.eclipse.dirigible.runtime.scheduler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class SchedulerActivator implements BundleActivator {
	
	static SchedulerServlet schedulerServlet;

	@Override
	public void start(BundleContext context) throws Exception {
		schedulerServlet = new SchedulerServlet();
		schedulerServlet.startSchedulers();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		schedulerServlet.stopSchedulers();
	}
	
	public static SchedulerServlet getSchedulerServlet() {
		return schedulerServlet;
	}

}
