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

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;

public class HtmlForEntityTemplateTitlePage extends FocusableWizardPage {

	private static final long serialVersionUID = 2666689807302575946L;

	private static final String INPUT_THE_PAGE_TITLE = Messages.HtmlForEntityTemplateTitlePage_INPUT_THE_PAGE_TITLE;

	private static final String PAGE_TITLE = Messages.HtmlForEntityTemplateTitlePage_PAGE_TITLE;

	private static final String SET_THE_PAGE_TITLE_WHICH_WILL_BE_USED_DURING_THE_GENERATION = Messages.HtmlForEntityTemplateTitlePage_SET_THE_PAGE_TITLE_WHICH_WILL_BE_USED_DURING_THE_GENERATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateParametersPage"; //$NON-NLS-1$

	private ContentForEntityModel model;

	private Text pageTitleText;

	public HtmlForEntityTemplateTitlePage(ContentForEntityModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(PAGE_TITLE);
		setDescription(SET_THE_PAGE_TITLE_WHICH_WILL_BE_USED_DURING_THE_GENERATION);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		createPageTitleField(composite);
		checkPageStatus();
	}

	private void createPageTitleField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(PAGE_TITLE);

		pageTitleText = new Text(parent, SWT.BORDER);
		pageTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		pageTitleText.addModifyListener(new ModifyListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1635488325025189360L;

			@Override
			public void modifyText(ModifyEvent event) {
				if (pageTitleText.getText() == null
						|| "".equals(pageTitleText.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_PAGE_TITLE);
				} else {
					setErrorMessage(null);
					model.setPageTitle(pageTitleText.getText());
				}
				checkPageStatus();
			}
		});
		setFocusable(pageTitleText);
	}

	private void checkPageStatus() {
		if (model.getPageTitle() == null || "".equals(model.getPageTitle())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}
		setPageComplete(true);
	}

}
