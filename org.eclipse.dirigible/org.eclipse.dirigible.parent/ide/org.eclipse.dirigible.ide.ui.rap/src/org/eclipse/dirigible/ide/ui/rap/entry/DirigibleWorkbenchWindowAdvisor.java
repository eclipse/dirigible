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

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class DirigibleWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private static final String WORKBENCH = Messages.DirigibleWorkbenchWindowAdvisor_WORKBENCH;

	public DirigibleWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new DirigibleActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(ICommonConstants.DIRIGIBLE_PRODUCT_NAME + WORKBENCH);

		configurer.setShellStyle(SWT.TITLE | SWT.MAX | SWT.RESIZE);
		configurer.setShowProgressIndicator(true);
		configurer.setShowStatusLine(true);
	}

	@Override
	public void postWindowOpen() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		Rectangle shellBounds = shell.getBounds();
		if (!shell.getMaximized() && (shellBounds.x == 0) && (shellBounds.y == 0)) {
			shell.setLocation(70, 25);
		}
	}

}
