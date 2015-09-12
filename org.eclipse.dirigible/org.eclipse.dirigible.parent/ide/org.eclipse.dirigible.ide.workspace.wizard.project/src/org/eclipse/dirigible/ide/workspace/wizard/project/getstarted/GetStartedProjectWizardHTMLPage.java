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

package org.eclipse.dirigible.ide.workspace.wizard.project.getstarted;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class GetStartedProjectWizardHTMLPage extends WizardPage {
	
	private static final Logger logger = Logger
			.getLogger(GetStartedProjectWizardHTMLPage.class);

	private static final String PAGE_DESCRIPTION = "Creation of a HTML5 Form";

	private static final String PAGE_TITLE = "HTML5 Form";

	private static final String PAGE_NAME = "HTML5 Form"; //$NON-NLS-1$

	private static final String CONTENT = "html5form.html"; //$NON-NLS-1$

	public GetStartedProjectWizardHTMLPage() {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new FillLayout());

		Browser welcomeLabel = new Browser(composite, SWT.NONE);
		String description = "";
		try {
			description = IOUtils.toString(
					GetStartedProjectWizardHTMLPage.class.getResourceAsStream(CONTENT));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		welcomeLabel.setText(description);

	}

	public void setWarningMessage(String message) {
		setMessage(message, WARNING);
	}

	public void setCanFinish(boolean value) {
		setPageComplete(value);
	}


}
