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

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class DirigibleWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String INITIAL_PERSPECTIVE_ID = "workspace"; //$NON-NLS-1$

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		String activeWorkspace = CommonIDEParameters.get("perspective"); //$NON-NLS-1$
		if (activeWorkspace != null) {
			return activeWorkspace;
		}
		return INITIAL_PERSPECTIVE_ID;
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer windowConfigurer) {
		return new DirigibleWorkbenchWindowAdvisor(windowConfigurer);
	}
}
