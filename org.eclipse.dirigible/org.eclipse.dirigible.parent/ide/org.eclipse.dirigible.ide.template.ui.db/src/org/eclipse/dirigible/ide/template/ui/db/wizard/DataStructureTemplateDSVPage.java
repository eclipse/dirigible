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

import java.util.Random;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.db.export.DataExportDialog;
import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap;
import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap.DataTypes;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableExporter;
import org.eclipse.dirigible.repository.ext.db.transfer.TableColumn;
import org.eclipse.dirigible.repository.ext.db.transfer.TableName;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DataStructureTemplateDSVPage extends WizardPage {

	private static final long serialVersionUID = 7697608637259213988L;

	private static final String AVAILABLE_TABLES = Messages.DataStructureTemplateDSVPage_0;

	private static final String DSV = Messages.DataStructureTemplateDSVPage_1;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateDSVPage"; //$NON-NLS-1$

	private static final String NO_TABLE_IS_SELECTED_PLEASE_SELECT_ONE = Messages.DataStructureTemplateDSVPage_2;

	private static final String GENERATE_DSV_SAMPLE_BASED_ON_TABLE = Messages.DataStructureTemplateDSVPage_3;

	private DataStructureTemplateModel model;

	private Label labelSelected;

	protected DataStructureTemplateDSVPage(DataStructureTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(DSV);
		setDescription(GENERATE_DSV_SAMPLE_BASED_ON_TABLE);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		createTablesList(composite);
		checkPageStatus();
	}

	private void createTablesList(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(AVAILABLE_TABLES);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

		final TableViewer typeViewer = DataExportDialog.createTableList(parent);
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				if ((typeViewer.getTable().getSelection() != null) && (typeViewer.getTable().getSelection().length > 0)) {
					TableName selectedTableName = (TableName) typeViewer.getTable().getSelection()[0].getData();
					if (selectedTableName != null) {
						model.setTableName(selectedTableName.getName());

						DBTableExporter dataFinder = new DBTableExporter(
								DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()));
						dataFinder.setTableName(selectedTableName.getName());
						dataFinder.getTableData();

						model.setDsvSampleRows(generateDsvSamplesRows(dataFinder.getTableColumns()));

						labelSelected.setText(selectedTableName.getName());
						labelSelected.pack();
					} else {
						model.setTableName(null);
						labelSelected.setText("");
						labelSelected.pack();
					}
				} else {
					model.setTableName(null);
				}

				checkPageStatus();
			}

			private String[] generateDsvSamplesRows(TableColumn[] tableColumns) {
				final String rowDelimiter = ";"; //$NON-NLS-1$
				final String dsvDelimiter = "|"; //$NON-NLS-1$
				StringBuilder dsvSample = new StringBuilder();
				int columnsCount = tableColumns.length;
				for (int i = 0; i < 3; i++) {
					for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
						TableColumn column = tableColumns[columnIndex];
						String sampleValue = getSampleValue(column);
						dsvSample.append(sampleValue);
						if (columnIndex < (columnsCount - 1)) {
							dsvSample.append(dsvDelimiter);
						}
					}
					dsvSample.append(rowDelimiter);
				}
				return dsvSample.toString().split(rowDelimiter);
			}

			private String getSampleValue(TableColumn column) {
				String type = DBSupportedTypesMap.getTypeName(column.getType());
				boolean numeric = type.equals(DataTypes.BIGINT.toString()) || type.equals(DataTypes.SMALLINT.toString())
						|| type.equals(DataTypes.BINARY.toString()) || type.equals(DataTypes.BIT.toString())
						|| type.equals(DataTypes.INTEGER.toString()) || type.equals(DataTypes.NUMERIC.toString())
						|| type.equals(DataTypes.TINYINT.toString());
				boolean blob = type.equals(DataTypes.BLOB.toString());
				boolean clob = type.equals(DataTypes.CLOB.toString());
				boolean booleanType = type.equals(DataTypes.BOOLEAN.toString());
				boolean textChar = type.equals(DataTypes.CHAR.toString());
				boolean textVarchar = type.equals(DataTypes.NVARCHAR.toString()) || type.equals(DataTypes.VARCHAR.toString());
				boolean date = type.equals(DataTypes.DATE.toString());

				boolean floatingPoint = type.equals(DataTypes.REAL.toString()) || type.equals(DataTypes.DECIMAL.toString())
						|| type.equals(DataTypes.DOUBLE.toString()) || type.equals(DataTypes.FLOAT.toString());
				boolean time = type.equals(DataTypes.TIME.toString());
				boolean timeStamp = type.equals(DataTypes.TIMESTAMP.toString());

				String value = null;
				Random rand = new Random();
				if (numeric) {
					value = Integer.toString(rand.nextInt(100) + 1);
				} else if (blob) {
					// TODO default value for BLOB
				} else if (clob) {
					// TODO default value for CLOB
				} else if (booleanType) {
					// TODO default value for Boolean
				} else if (time) {
					value = "10:30:45"; //$NON-NLS-1$
				} else if (timeStamp) {
					value = "2014-02-19 10:30:45"; //$NON-NLS-1$
				} else if (textChar) {
					value = "J"; //$NON-NLS-1$
				} else if (textVarchar) {
					value = "Test" + rand.nextInt(100); //$NON-NLS-1$
				} else if (date) {
					value = "2014-02-19"; //$NON-NLS-1$
				} else if (floatingPoint) {
					value = Float.toString(rand.nextFloat());
				} else {
					value = DataTypes.UNSUPPORTED_TYPE.toString();
				}

				return value;
			}
		});
		labelSelected = new Label(parent, SWT.NONE);
		labelSelected.setText("");
		labelSelected.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
	}

	private void checkPageStatus() {
		if ((model.getTableName() == null) || "".equals(model.getTableName())) { //$NON-NLS-1$
			setErrorMessage(NO_TABLE_IS_SELECTED_PLEASE_SELECT_ONE);
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

}
