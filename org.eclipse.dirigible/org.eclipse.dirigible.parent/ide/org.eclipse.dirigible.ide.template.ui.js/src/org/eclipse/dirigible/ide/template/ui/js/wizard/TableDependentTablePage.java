/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.wizard;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TableDependentTablePage extends WizardPage {

	private static final String SELECT_THE_DEPENDENT_COLUMN_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.TableDependentTablePage_SELECT_THE_DEPENDENT_COLUMN_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String SELECT_DEPENDENT_COLUMN = Messages.TableDependentTablePage_SELECT_DEPENDENT_COLUMN;

	private static final Logger logger = Logger.getLogger(TableDependentTablePage.class);

	public static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.js.wizard.TableDependentTablePage"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	private ComboViewer viewer;

	public ComboViewer getViewer() {
		return viewer;
	}

	protected TableDependentTablePage(JavascriptServiceTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECT_DEPENDENT_COLUMN);
		setDescription(SELECT_THE_DEPENDENT_COLUMN_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		createColumnsField(composite);

		checkPageStatus();
	}

	private void createColumnsField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(SELECT_DEPENDENT_COLUMN);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

		viewer = new ComboViewer(parent, SWT.READ_ONLY);

		viewer.setContentProvider(ArrayContentProvider.getInstance());

		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TableColumn) {
					TableColumn current = (TableColumn) element;
					return current.getName();
				}
				return super.getText(element);
			}
		});

		final TableColumn[] columns = model.getTableColumns();
		viewer.setInput(columns);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TableColumn column = (TableColumn) selection.getFirstElement();
				model.setDependentColumn(column.getName());
				viewer.refresh();
				checkPageStatus();
			}
		});

		// viewer.setSelection(new StructuredSelection(viewer.getElementAt(0)), true);
	}

	private void checkPageStatus() {
		setPageComplete(model.validateDependentColumn());
	}

}
