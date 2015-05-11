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

package org.eclipse.dirigible.ide.workspace.wizard.project.export;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DownloadDialog extends Dialog {

	private static final long serialVersionUID = -380351602370874305L;

	Browser b;
	String url;

	public DownloadDialog(Shell parent) {
		super(parent);
	}

	public void setURL(String url) {
		if (b != null && !b.isDisposed()) {
			b.setUrl(url);
		}
		this.url = url;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		b = new Browser(parent, SWT.NONE);
		if (url != null) {
			b.setUrl(url);
		}
		return control;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return null;
	}

	@Override
	protected int getShellStyle() {
		return SWT.NO_TRIM;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(1, 1);
		newShell.setMinimized(true);
	}
}
