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
import java.util.Collection;
import java.util.List;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.io.ProxyUtils;
import org.eclipse.dirigible.ide.ui.rap.api.DirigibleWorkbenchInitializersManager;
import org.eclipse.dirigible.ide.ui.rap.api.IDirigibleWorkbenchInitializer;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Main entry point to the Dirigible workbench
 */
public class DirigibleWorkbench extends AbstractEntryPoint {

	private static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT = Messages.DirigibleWorkbench_ARE_YOU_SURE_YOU_WANT_TO_QUIT;

	private static final Logger logger = Logger.getLogger(DirigibleWorkbench.class);

	@Override
	public int createUI() {
		final Display display = PlatformUI.createDisplay();

		createContents(display.getActiveShell());

		ExitConfirmation service = (ExitConfirmation) CommonIDEParameters.getService(ExitConfirmation.class);
		service.setMessage(String.format(ARE_YOU_SURE_YOU_WANT_TO_QUIT, ICommonConstants.DIRIGIBLE_PRODUCT_NAME));

		try {
			ProxyUtils.setProxySettings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return PlatformUI.createAndRunWorkbench(display, new DirigibleWorkbenchAdvisor());
	}

	@Override
	protected void createContents(Composite parent) {

		// set the parameter from the request
		Collection<String> paramaters = getParameterNames();
		for (String parameter : paramaters) {
			CommonIDEParameters.set(parameter, getParameter(parameter));
		}

		List<IDirigibleWorkbenchInitializer> initializersList = DirigibleWorkbenchInitializersManager.getInitializers();
		for (IDirigibleWorkbenchInitializer initializer : initializersList) {
			initializer.doInitialization();
		}

	}

}
