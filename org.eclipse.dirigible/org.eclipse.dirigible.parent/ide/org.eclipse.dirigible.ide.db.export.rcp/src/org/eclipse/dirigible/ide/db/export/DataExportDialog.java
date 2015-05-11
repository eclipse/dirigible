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

package org.eclipse.dirigible.ide.db.export;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.dirigible.ide.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.logging.Logger;

public class DataExportDialog extends TitleAreaDialog {

	private static final long serialVersionUID = 1L;

	private static final String VIEW = "VIEW";
	private static final String TABLE = "TABLE";
	private static final String CLOSE = "Close";
	private static final String EXPORT = "Export";
	private static final String EXPORT_DATA = "Export Data";
	private static final String TABLE_TYPE = "TABLE_TYPE";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String EXPORT_AS_DSV_FILE = "Export as DSV File";
	private static final String SELECT_AVAILABLE_TABLE_FORM_THE_LIST = "Select Available Table From The List";
	private static final String AVAILABLE_TABLES_AND_VIEWS = Messages.DataExportDialog_AVAILABLE_TABLES_AND_VIEWS;
	private static final String ERROR_ON_LOADING_TABLES_FROM_DATABASE_FOR_GENERATION = Messages.DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV;

	private Button btnExport;
	private String selectedTableName;

	private static final Logger logger = Logger.getLogger(DataExportDialog.class);

	public DataExportDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(EXPORT_DATA);
		setMessage(SELECT_AVAILABLE_TABLE_FORM_THE_LIST, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		final TableViewer typeViewer = createTableList(container);
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				int selectionIndex = typeViewer.getTable().getSelectionIndex();
				TableName[] tables = (TableName[]) typeViewer.getInput();
				if (selectionIndex >= 0) {
					setSelectedTableName(tables[selectionIndex].getName());
					btnExport.setEnabled(true);
				} else {
					btnExport.setEnabled(false);
				}
			}
		});
		;
		createExportButton(container);

		return area;
	}

	public static TableViewer createTableList(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(AVAILABLE_TABLES_AND_VIEWS);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.UP, false, false));

		TableViewer typeViewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		typeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new TablesTemplateTablePageLabelProvider());
		typeViewer.setSorter(new ViewerSorter());
		typeViewer.setInput(getAvailableTables());
		return typeViewer;
	}

	private static TableName[] getAvailableTables() {
		List<TableName> availableTableNames = new ArrayList<TableName>();

		try {
			Connection connection = null;
			try {
				connection = DataSourceFacade.getInstance().getDataSource().getConnection();
				DatabaseMetaData meta = connection.getMetaData();
				ResultSet tableNames = meta.getTables(null, null, "%", null); //$NON-NLS-1$

				while (tableNames.next()) {
					String sTableName = tableNames.getString(TABLE_NAME); //$NON-NLS-1$
					String sTableType = tableNames.getString(TABLE_TYPE); //$NON-NLS-1$
					if (TABLE.equals(sTableType) || VIEW.equals(sTableType)) { //$NON-NLS-1$ //$NON-NLS-2$
						TableName tableName = new TableName(sTableName, sTableType);
						availableTableNames.add(tableName);
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			logger.error(ERROR_ON_LOADING_TABLES_FROM_DATABASE_FOR_GENERATION, e);
		}
		return availableTableNames.toArray(new TableName[] {});
	}

	private void createExportButton(Composite container) {
		Label lbtExport = new Label(container, SWT.NONE);
		lbtExport.setText(EXPORT_AS_DSV_FILE);

		GridData dataSuffix = new GridData();
		dataSuffix.grabExcessHorizontalSpace = false;
		dataSuffix.horizontalAlignment = GridData.BEGINNING;
		btnExport = new Button(container, SWT.PUSH);
		btnExport.setLayoutData(dataSuffix);
		btnExport.setText(EXPORT);
		btnExport.setEnabled(false);
		btnExport.addSelectionListener(new SelectionListener() {

			private static final long serialVersionUID = 1139810430273810538L;

			@Override
			public void widgetSelected(SelectionEvent e) {

				DataDownloadDialog dataDownloadDialog = new DataDownloadDialog(e.display
						.getActiveShell());
				// TODO
//				dataDownloadDialog.setURL(DataExportServiceHandler.getUrl(getSelectedTableName()));
				dataDownloadDialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	private void saveInput() {
		// TODO Auto-generated method stub
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, CLOSE, true);
	}

	public String getSelectedTableName() {
		return selectedTableName;
	}

	public void setSelectedTableName(String tableName) {
		selectedTableName = tableName;
	}
}
