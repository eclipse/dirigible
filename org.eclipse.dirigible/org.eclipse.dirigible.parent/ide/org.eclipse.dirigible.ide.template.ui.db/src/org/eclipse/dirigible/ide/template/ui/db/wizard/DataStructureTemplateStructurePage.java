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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class DataStructureTemplateStructurePage extends FocusableWizardPage {

	private static final long serialVersionUID = -1988896787139142411L;

	private static final String REMOVE = Messages.DataStructureTemplateStructurePage_REMOVE;

	private static final String ADD = Messages.DataStructureTemplateStructurePage_ADD;

	private static final String TREE_DEFAULT = Messages.DataStructureTemplateStructurePage_TREE_DEFAULT;

	private static final String TREE_PK = Messages.DataStructureTemplateStructurePage_TREE_PK;

	private static final String TREE_NN = Messages.DataStructureTemplateStructurePage_TREE_NN;

	private static final String TREE_LENGTH = Messages.DataStructureTemplateStructurePage_TREE_LENGTH;

	private static final String TREE_TYPE = Messages.DataStructureTemplateStructurePage_TREE_TYPE;

	private static final String TREE_NAME = Messages.DataStructureTemplateStructurePage_TREE_NAME;

	private static final String COLUMN_DEFINITIONS = Messages.DataStructureTemplateStructurePage_COLUMN_DEFINITIONS;

	private static final String ADD_COLUMN_DEFINITIONS_FOR_THE_SELECTED_DATA_STRUCTURE = Messages.DataStructureTemplateStructurePage_ADD_COLUMN_DEFINITIONS_FOR_THE_SELECTED_DATA_STRUCTURE;

	private static final String DEFINITION_OF_COLUMNS = Messages.DataStructureTemplateStructurePage_DEFINITION_OF_COLUMNS;

	private static final String REMOVE_COLUMN = Messages.DataStructureTemplateStructurePage_REMOVE_COLUMN;

	private static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_COLUMN = Messages.DataStructureTemplateStructurePage_ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_COLUMN;

	// private static final Logger logger = Logger
	// .getLogger(DataStructureTemplateStructurePage.class);

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateStructurePage"; //$NON-NLS-1$

	private TableTemplateModel model;

	private TreeViewer typeViewer;

	private Button addButton;

	private Button removeButton;

	private ColumnDefinition[] columnDefinitions;

	protected DataStructureTemplateStructurePage(TableTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(DEFINITION_OF_COLUMNS);
		setDescription(ADD_COLUMN_DEFINITIONS_FOR_THE_SELECTED_DATA_STRUCTURE);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		createColumnsField(composite);
		final Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new RowLayout());
		createButtonsField(compositeButtons);

		checkPageStatus();
		// hide all error messages initially (independently from 'page complete'
		// state)
		hideErrorMessages();
	}

	class DataStructureTemplateStructurePageViewContentProvider implements ITreeContentProvider {
		/**
		 *
		 */
		private static final long serialVersionUID = 9053893213315991958L;

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			//
		}

		@Override
		public void dispose() {
			//
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				return ((Collection<?>) inputElement).toArray();
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	private void createColumnsField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(COLUMN_DEFINITIONS);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

		typeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		typeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		typeViewer.setContentProvider(new DataStructureTemplateStructurePageViewContentProvider());
		typeViewer.setLabelProvider(new DataStructureTemplateStructurePageLabelProvider());
		Tree tree = typeViewer.getTree();
		tree.setHeaderVisible(true);
		createTreeHeader(tree);
		columnDefinitions = createColumnDefinitions();
		typeViewer.setInput(columnDefinitions);
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateColumnDefinitions();
				checkPageStatus();
			}
		});
		updateColumnDefinitions();
		checkPageStatus();

	}

	private void createTreeHeader(Tree tree) {
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(TREE_NAME);
		column.setWidth(150);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(TREE_TYPE);
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.RIGHT);
		column.setText(TREE_LENGTH);
		column.setWidth(70);
		column = new TreeColumn(tree, SWT.CENTER);
		column.setText(TREE_NN);
		column.setWidth(50);
		column = new TreeColumn(tree, SWT.CENTER);
		column.setText(TREE_PK);
		column.setWidth(50);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(TREE_DEFAULT);
		column.setWidth(100);
	}

	private void createButtonsField(Composite parent) {
		addButton = new Button(parent, SWT.BORDER);
		addButton.setText(ADD);
		addButton.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 7621468345508014093L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				addClickedEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addClickedEvent();
			}
		});
		setFocusable(addButton);

		removeButton = new Button(parent, SWT.BORDER);
		removeButton.setText(REMOVE);
		removeButton.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = -301365843089640919L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeClicked();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				removeClicked();
			}
		});

	}

	private void addClickedEvent() {
		setEnabled(false);
		addClicked();
		setEnabled(true);
	}

	private void addClicked() {
		ColumnDefinition columnDefinition = new ColumnDefinition();
		AddColumnDialog addColumnDialog = new AddColumnDialog(columnDefinition, columnDefinitions.clone(), getShell());
		int result = addColumnDialog.open();
		if (result == Dialog.OK) {
			columnDefinitions = (ColumnDefinition[]) typeViewer.getInput();
			columnDefinitions = Arrays.copyOf(columnDefinitions, columnDefinitions.length + 1);
			columnDefinitions[columnDefinitions.length - 1] = columnDefinition;
			typeViewer.setInput(columnDefinitions);
			updateColumnDefinitions();
			checkPageStatus();
		}
	}

	private void removeClicked() {
		TreeItem[] selection = typeViewer.getTree().getSelection();
		List<Integer> removeIndexes = new ArrayList<Integer>();

		if ((selection != null) && (selection.length > 0)
				&& MessageDialog.openQuestion(null, REMOVE_COLUMN, ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_COLUMN)) {

			for (TreeItem nextSelection : selection) {
				removeIndexes.add(typeViewer.getTree().indexOf(nextSelection));
			}

			columnDefinitions = (ColumnDefinition[]) typeViewer.getInput();
			columnDefinitions = removeColumnsFromTable(columnDefinitions, removeIndexes);
			typeViewer.setInput(columnDefinitions);
			updateColumnDefinitions();
			checkPageStatus();
		}
	}

	private ColumnDefinition[] removeColumnsFromTable(ColumnDefinition[] columns, List<Integer> removeIndexes) {
		Map<Integer, ColumnDefinition> columnDefinitions = new HashMap<Integer, ColumnDefinition>();
		for (int i = 0; i < columns.length; i++) {
			columnDefinitions.put(i, columns[i]);
		}
		for (Integer removeIndex : removeIndexes) {
			columnDefinitions.remove(removeIndex);
		}
		Collection<ColumnDefinition> values = columnDefinitions.values();
		return values.toArray(new ColumnDefinition[values.size()]);
	}

	private void updateColumnDefinitions() {
		ColumnDefinition[] columnDefinitions = (ColumnDefinition[]) typeViewer.getInput();
		model.setColumnDefinitions(columnDefinitions);
	}

	private ColumnDefinition[] createColumnDefinitions() {
		List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
		return columnDefinitions.toArray(new ColumnDefinition[] {});
	}

	private void checkPageStatus() {
		IValidationStatus status = model.validateColumnDefinitions();
		if (status.hasErrors()) {
			setErrorMessage(status.getMessage());
			setPageComplete(false);
		} else if (status.hasWarnings()) {
			setErrorMessage(status.getMessage());
			setPageComplete(true);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	/**
	 * Hides all error messages from the UI.
	 */
	private void hideErrorMessages() {
		setErrorMessage(null);
	}
}
