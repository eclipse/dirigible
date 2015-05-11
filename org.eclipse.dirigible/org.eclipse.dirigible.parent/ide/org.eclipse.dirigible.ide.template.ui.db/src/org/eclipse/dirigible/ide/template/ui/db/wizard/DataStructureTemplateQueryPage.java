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

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;

public class DataStructureTemplateQueryPage extends FocusableWizardPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7818351982843742829L;

	private static final String QUERY = Messages.DataStructureTemplateQueryPage_QUERY;

	private static final String INPUT_THE_SQL_QUERY_FOR_THE_VIEW = Messages.DataStructureTemplateQueryPage_INPUT_THE_SQL_QUERY_FOR_THE_VIEW;

	private static final String SET_THE_QUERY_FOR_THE_VIEW_WHICH_WILL_BE_USED_DURING_THE_GENERATION = Messages.DataStructureTemplateQueryPage_SET_THE_QUERY_FOR_THE_VIEW_WHICH_WILL_BE_USED_DURING_THE_GENERATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateQueryPage"; //$NON-NLS-1$

	private DataStructureTemplateModel model;

	private Composite composite;

	private Text queryText;

	protected DataStructureTemplateQueryPage(DataStructureTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(QUERY);
		setDescription(SET_THE_QUERY_FOR_THE_VIEW_WHICH_WILL_BE_USED_DURING_THE_GENERATION);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		createIdField(composite);
		checkPageStatus();
	}
	
	private void createIdField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(QUERY);

		queryText = new Text(parent, SWT.BORDER);
		queryText
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		queryText.addModifyListener(new ModifyListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5393849992282223675L;

			@Override
			public void modifyText(ModifyEvent event) {
				if (queryText.getText() == null
						|| "".equals(queryText.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_SQL_QUERY_FOR_THE_VIEW);
				} else {
					setErrorMessage(null);
					model.setQuery(queryText.getText());
				}
				checkPageStatus();
			}
		});
		setFocusable(queryText);
	}

	private void checkPageStatus() {
		if (model.getQuery() == null || "".equals(model.getQuery())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}

		setPageComplete(true);
	}

}
