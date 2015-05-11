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

package org.eclipse.dirigible.ide.ui.rap.entry;

import java.io.IOException;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.io.ProxyUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class DirigibleWorkbench implements EntryPoint {

	private static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT = Messages.DirigibleWorkbench_ARE_YOU_SURE_YOU_WANT_TO_QUIT;
	
	private static final Logger logger = Logger.getLogger(DirigibleWorkbench.class); 

	public int createUI() {
		final Display display = PlatformUI.createDisplay();
		// Disabled because we do not want to show confirmation dialog.
		ExitConfirmation service = (ExitConfirmation) CommonParameters.getService(
				ExitConfirmation.class);
		service.setMessage(ARE_YOU_SURE_YOU_WANT_TO_QUIT
				+ ICommonConstants.DIRIGIBLE_PRODUCT_NAME); // TODO: I18N
		
		try {
			ProxyUtils.setProxySettings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return PlatformUI.createAndRunWorkbench(display,
				new DirigibleWorkbenchAdvisor());
	}

}
