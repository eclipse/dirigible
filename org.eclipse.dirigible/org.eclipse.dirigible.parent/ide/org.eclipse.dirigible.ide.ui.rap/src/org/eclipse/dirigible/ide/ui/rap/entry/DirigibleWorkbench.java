/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.ui.rap.entry;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.io.ProxyUtils;
import org.eclipse.dirigible.ide.ui.rap.api.DirigibleWorkbenchInitializersManager;
import org.eclipse.dirigible.ide.ui.rap.api.IDirigibleWorkbenchInitializer;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Main entry point to the Dirigible workbench
 */
public class DirigibleWorkbench implements EntryPoint {

	private static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT = Messages.DirigibleWorkbench_ARE_YOU_SURE_YOU_WANT_TO_QUIT;

	private static final Logger logger = Logger.getLogger(DirigibleWorkbench.class);

	@Override
	public int createUI() {
		final Display display = PlatformUI.createDisplay();

		// set the parameter from the request
		Enumeration<String> paramaters = RWT.getRequest().getParameterNames();
		while (paramaters.hasMoreElements()) {
			String parameter = paramaters.nextElement();
			CommonParameters.set(parameter, RWT.getRequest().getParameter(parameter));
		}

		List<IDirigibleWorkbenchInitializer> initializersList = DirigibleWorkbenchInitializersManager.getInitializers();
		for (IDirigibleWorkbenchInitializer initializer : initializersList) {
			initializer.doInitialization();
		}

		ExitConfirmation service = (ExitConfirmation) CommonParameters.getService(ExitConfirmation.class);
		service.setMessage(String.format(ARE_YOU_SURE_YOU_WANT_TO_QUIT, ICommonConstants.DIRIGIBLE_PRODUCT_NAME));

		try {
			ProxyUtils.setProxySettings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return PlatformUI.createAndRunWorkbench(display, new DirigibleWorkbenchAdvisor());
	}

}
