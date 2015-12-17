/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.publish.ui.command;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Auto activation action
 */
public class AutoActivateAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		CommonParameters.setAutoActivate(action.isChecked());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	private static String AUTO_ACTIVATOR = "auto-activator";

	@Override
	public void init(IWorkbenchWindow window) {
		// WorkspaceLocator.getWorkspace().addResourceChangeListener(this);
		AutoActivator autoActivator = (AutoActivator) CommonParameters.getObject(AUTO_ACTIVATOR);
		if (autoActivator == null) {
			autoActivator = new AutoActivator();
			autoActivator.registerListener();
			CommonParameters.setObject(AUTO_ACTIVATOR, autoActivator);
		}
	}

}
