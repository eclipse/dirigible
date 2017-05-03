/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.air.init;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Wrapper MasterRepositorySynchronizerServlet
 */
@WebServlet(name = "SchedulerServlet", urlPatterns = "/services/scheduler", loadOnStartup = 10)
public class SchedulerServlet extends org.eclipse.dirigible.runtime.scheduler.SchedulerServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		startSchedulers();
	}

	@Override
	public void destroy() {
		stopSchedulers();
	}
}
