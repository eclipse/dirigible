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

package org.eclipse.dirigible.ide.workspace.ui.shared;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public abstract class FocusableWizardPage extends WizardPage {
	private static final long serialVersionUID = -3674212416615171129L;

	private Control control;
	private boolean enabled;

	protected FocusableWizardPage(String pageName) {
		super(pageName);
		this.enabled = true;
	}

	protected void setFocusable(Control control) {
		this.control = control;
	}

	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
		getContainer().updateButtons();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return enabled ? super.getPreviousPage() : null;
	}

	@Override
	public IWizardPage getNextPage() {
		return enabled ? super.getNextPage() : null;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		control.setFocus();
		if (control instanceof Text) {
			((Text) control).selectAll();
		}
	}
}
