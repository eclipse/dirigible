/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.debug.ui;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.ui.widget.extbrowser.ExtendedBrowser;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class SessionsWSView extends Viewer {

	private static String DEBUG_SESSIONS_LOCATION;

	static {
		DEBUG_SESSIONS_LOCATION = ((CommonParameters.getRuntimeUrl() == null) || "".equals(CommonParameters.getRuntimeUrl()))
				? "/ui/debugws/debugsessions.html" : "/services/ui/debugws/debugsessions.html";
	}

	private ExtendedBrowser browser = null;

	public SessionsWSView(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		browser = new ExtendedBrowser(parent, SWT.NONE);
		browser.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(DEBUG_SESSIONS_LOCATION);
	}

	@Override
	public Control getControl() {
		return this.browser.getControl();
	}

	@Override
	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInput(Object input) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		// TODO Auto-generated method stub

	}
}
