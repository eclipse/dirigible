/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HtmlForEntityTemplateTablePage extends WizardPage {

	private static final String BTN_DESELECT_ALL = "&Deselect All"; //$NON-NLS-1$

	private static final String BTN_SELECT_ALL = "&Select All"; //$NON-NLS-1$

	private static final String ES_TABLE_TYPE = "tableType"; //$NON-NLS-1$

	private static final String ES_TABLE_NAME = "tableName"; //$NON-NLS-1$

	private static final String DB_COLUMN_SIZE = "COLUMN_SIZE"; //$NON-NLS-1$

	private static final String DB_TYPE_NAME = "TYPE_NAME"; //$NON-NLS-1$

	private static final String DB_COLUMN_NAME = "COLUMN_NAME"; //$NON-NLS-1$

	private static final String COLUMN_LABEL = "Label"; //$NON-NLS-1$

	private static final String COLUMN_WIDGET = "Widget"; //$NON-NLS-1$

	private static final String COLUMN_SIZE = "Size"; //$NON-NLS-1$

	private static final String COLUMN_TYPE = "Type"; //$NON-NLS-1$

	private static final String COLUMN_NAME = "Name"; //$NON-NLS-1$

	private static final String AVAILABLE_FIELDS = "Available Fields"; //$NON-NLS-1$

	private static final String ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_GENERATION = "Error on Loading Table Columns from Database for Generation"; //$NON-NLS-1$

	private static final String SELECT_THE_VISIBLE_FIELDS_WHICH_WILL_BE_USED_DURING_GENERATION = "Select the visible fields which will be used during generation"; //$NON-NLS-1$

	private static final String SELECTION_OF_FIELDS = "Selection of Fields"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(HtmlForEntityTemplateTablePage.class);

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateTablePage"; //$NON-NLS-1$

	private HtmlForEntityTemplateModel model;

	private TableViewer typeViewer;

	protected HtmlForEntityTemplateTablePage(HtmlForEntityTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_FIELDS);
		setDescription(SELECT_THE_VISIBLE_FIELDS_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		createTableField(composite);

		checkPageStatus();
	}

	private void selectAll(boolean b) {
		TableColumn[] tableColumns = (TableColumn[]) typeViewer.getInput();
		TableItem[] items = typeViewer.getTable().getItems();
		int selectionIndex = typeViewer.getTable().getSelectionIndex();
		for (int j = 0; j < items.length; j++) {
			TableItem tblItem = items[j];
			typeViewer.getTable().setSelection(j);
			tableColumns[j].setVisible(tblItem.getChecked());
			items[typeViewer.getTable().getSelectionIndex()].setChecked(b || tableColumns[j].isKey());
			tableColumns[j].setVisible(true);
		}
		typeViewer.getTable().setSelection(selectionIndex);
	};

	private void createTableField(Composite parent) {
		Composite upperPart = new Composite(parent, SWT.NONE);
		upperPart.setLayout(new GridLayout(3, false));
		upperPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Label label = new Label(upperPart, SWT.NONE);
		label.setText(AVAILABLE_FIELDS);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));

		Button selectButton = new Button(upperPart, SWT.PUSH);
		selectButton.setText(BTN_SELECT_ALL);
		selectButton.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		selectButton.setFont(JFaceResources.getDialogFont());
		selectButton.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = -7095187791495950403L;

			@Override
			public void widgetSelected(SelectionEvent event) {
				selectAll(true);
			}
		});

		Button deselectButton = new Button(upperPart, SWT.PUSH);
		deselectButton.setText(BTN_DESELECT_ALL);
		deselectButton.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		deselectButton.setFont(JFaceResources.getDialogFont());
		deselectButton.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 7117397741755265980L;

			@Override
			public void widgetSelected(SelectionEvent event) {
				selectAll(false);
			}
		});

		typeViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.CHECK);

		/// make lines and header visible
		final Table table = typeViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// create a column for the name
		TableViewerColumn colName = new TableViewerColumn(typeViewer, SWT.NONE);
		colName.getColumn().setWidth(200);
		colName.getColumn().setText(COLUMN_NAME);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumn column = (TableColumn) element;
				return column.getName();
			}
		});

		// create a column for the type
		TableViewerColumn colType = new TableViewerColumn(typeViewer, SWT.NONE);
		colType.getColumn().setWidth(100);
		colType.getColumn().setText(COLUMN_TYPE);
		colType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumn column = (TableColumn) element;
				return column.getType();
			}
		});

		// create a column for the size
		TableViewerColumn colSize = new TableViewerColumn(typeViewer, SWT.NONE);
		colSize.getColumn().setWidth(50);
		colSize.getColumn().setText(COLUMN_SIZE);
		colSize.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumn column = (TableColumn) element;
				return column.getSize() + "";
			}
		});

		// create a column for the widgetType
		TableViewerColumn colWidgetType = new TableViewerColumn(typeViewer, SWT.NONE);
		colWidgetType.getColumn().setWidth(100);
		colWidgetType.getColumn().setText(COLUMN_WIDGET);
		colWidgetType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumn column = (TableColumn) element;
				return column.getWidgetType();
			}
		});

		EditingSupport widgetEditor = new WidgetTypeCellEditor(colWidgetType.getViewer());
		colWidgetType.setEditingSupport(widgetEditor);

		typeViewer.getColumnViewerEditor().addEditorActivationListener(new ColumnViewerEditorActivationListener() {

			@Override
			public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
				//
			}

			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
				//
			}

			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
				typeViewer.refresh();
			}

			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
				//
			}
		});

		// create a column for the label
		TableViewerColumn colLabel = new TableViewerColumn(typeViewer, SWT.NONE);
		colLabel.getColumn().setWidth(200);
		colLabel.getColumn().setText(COLUMN_LABEL);
		colLabel.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumn column = (TableColumn) element;
				return column.getSize() + "";
			}
		});

		EditingSupport labelEditor = new LabelCellEditor(colLabel.getViewer());
		colLabel.setEditingSupport(labelEditor);

		typeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new HtmlForEntityTemplateTablePageLabelProvider());
		// typeViewer.setSorter(new ViewerSorter());
		createTableColumns();
		typeViewer.setInput(model.getOriginalTableColumns());
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateTableColumns();
				checkPageStatus();
			}
		});
		updateTableColumns();

	}

	private void updateTableColumns() {
		TableColumn[] tableColumns = (TableColumn[]) typeViewer.getInput();
		TableItem[] items = typeViewer.getTable().getItems();
		int selectionIndex = typeViewer.getTable().getSelectionIndex();
		for (int j = 0; j < items.length; j++) {
			TableItem tblItem = items[j];
			typeViewer.getTable().setSelection(j);
			tableColumns[j].setVisible(tblItem.getChecked());
			if (tableColumns[j].isKey()) {
				items[typeViewer.getTable().getSelectionIndex()].setChecked(true);
				tableColumns[j].setVisible(true);
			}
		}
		typeViewer.getTable().setSelection(selectionIndex);
	}

	private void createTableColumns() {
		try {
			parseEntityDescriptor();

			Connection connection = getConnection();
			try {
				List<TableColumn> availableTableColumns = new ArrayList<TableColumn>();

				ResultSet primaryKeys = DBUtils.getPrimaryKeys(connection, model.getTableName());
				List<String> primaryKeysList = new ArrayList<String>();
				while (primaryKeys.next()) {
					String columnName = primaryKeys.getString(DB_COLUMN_NAME);
					// TableColumn tableColumn = new TableColumn(columnName,
					// true, true, null, 0);
					// availableTableColumns.add(tableColumn);
					primaryKeysList.add(columnName);
				}

				ResultSet columns = DBUtils.getColumns(connection, model.getTableName());
				while (columns.next()) {
					// columns
					String columnName = columns.getString(DB_COLUMN_NAME);
					String columnType = columns.getString(DB_TYPE_NAME);
					int columnSize = columns.getInt(DB_COLUMN_SIZE);

					TableColumn tableColumn = null;
					if (primaryKeysList.contains(columnName)) {
						tableColumn = new TableColumn(columnName, true, true, columnType, columnSize, WidgetType.WT_TEXT, columnName);
					} else {
						tableColumn = new TableColumn(columnName, false, true, columnType, columnSize, WidgetType.WT_TEXT, columnName);
					}
					if (!exists(availableTableColumns, tableColumn)) {
						availableTableColumns.add(tableColumn);
					}

				}

				model.setTableColumns(availableTableColumns.toArray(new TableColumn[] {}));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			logger.error(ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_GENERATION, e);
		}
	}

	private boolean exists(List<TableColumn> availableTableColumns, TableColumn tableColumn) {
		for (TableColumn tableColumn2 : availableTableColumns) {
			TableColumn tableColumnX = tableColumn2;
			if (tableColumnX.getName().equals(tableColumn.getName())) {
				return true;
			}
		}
		return false;
	}

	protected void parseEntityDescriptor() throws Exception {
		// {"tableName":"SOME_TABLE","tableType":"TABLE"}
		JsonParser parser = new JsonParser();
		JsonObject entityService = (JsonObject) parser.parse(new InputStreamReader(model.getSourceFile().getContents(), ICommonConstants.UTF8));
		model.setTableName(entityService.get(ES_TABLE_NAME).getAsString());
		model.setTableType(entityService.get(ES_TABLE_TYPE).getAsString());
	}

	private void checkPageStatus() {
		setPageComplete(model.validateTableColumns());
	}

	public HtmlForEntityTemplateModel getModel() {
		return model;
	}

	protected Connection getConnection() throws SQLException {
		try {
			return DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()).getConnection();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private final class WidgetTypeCellEditor extends EditingSupport {

		private ComboBoxViewerCellEditor cellEditor = null;

		private WidgetTypeCellEditor(ColumnViewer viewer) {
			super(viewer);
			cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			cellEditor.setLabelProvider(new LabelProvider());
			cellEditor.setContenProvider(new ArrayContentProvider());
			cellEditor.setInput(new String[] { WidgetType.WT_TEXT, WidgetType.WT_TEXTAREA, WidgetType.WT_DATE, WidgetType.WT_DROPDOWN,
					WidgetType.WT_LIST, WidgetType.WT_INTEGER, WidgetType.WT_FLOAT });
		}

		@Override
		protected void setValue(Object element, Object value) {
			if ((element instanceof TableColumn) && (value instanceof String)) {
				TableColumn column = (TableColumn) element;
				String newValue = (String) value;
				if (!newValue.equals(column.getWidgetType())) {
					column.setWidgetType(newValue);
				}
			}
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof TableColumn) {
				TableColumn column = (TableColumn) element;
				return column.getWidgetType();
			}
			return null;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}

	private final class LabelCellEditor extends EditingSupport {

		private TextCellEditor cellEditor = null;

		private LabelCellEditor(ColumnViewer viewer) {
			super(viewer);
			cellEditor = new TextCellEditor((Composite) getViewer().getControl(), SWT.SINGLE);
		}

		@Override
		protected void setValue(Object element, Object value) {
			if ((element instanceof TableColumn) && (value instanceof String)) {
				TableColumn column = (TableColumn) element;
				String newValue = (String) value;
				if (!newValue.equals(column.getLabel())) {
					column.setLabel(newValue);
				}
			}
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof TableColumn) {
				TableColumn column = (TableColumn) element;
				return column.getLabel();
			}
			return null;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}

}
