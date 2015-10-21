/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.dirigible.ide.editor.ace.EditorWidget;
import org.eclipse.dirigible.ide.editor.text.editor.EditorMode;
import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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

	private EditorWidget queryText;

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
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		label.setText(QUERY);

		// queryText = new Text(parent, SWT.BORDER);

		queryText = new EditorWidget(parent);
		queryText.setText("", EditorMode.SQL, false, false, 0);

		queryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// queryText.addKeyListener(new KeyListener() {
		//
		// private static final long serialVersionUID = 3767429446887374077L;
		//
		// @Override
		// public void keyReleased(KeyEvent e) {
		// //
		// }
		//
		// @Override
		// public void keyPressed(KeyEvent e) {
		// if (queryText.getText() == null
		// || "".equals(queryText.getText())) { //$NON-NLS-1$
		// setErrorMessage(INPUT_THE_SQL_QUERY_FOR_THE_VIEW);
		// } else {
		// setErrorMessage(null);
		// model.setQuery(queryText.getText());
		// }
		// checkPageStatus();
		//
		// }
		// });

		setFocusable(queryText);
	}

	private void checkPageStatus() {
		// if (model.getQuery() == null || "".equals(model.getQuery())) { //$NON-NLS-1$
		// setPageComplete(false);
		// return;
		// }

		setPageComplete(true);
	}

	public String getQuery() {
		return queryText.getText();
	}

}
