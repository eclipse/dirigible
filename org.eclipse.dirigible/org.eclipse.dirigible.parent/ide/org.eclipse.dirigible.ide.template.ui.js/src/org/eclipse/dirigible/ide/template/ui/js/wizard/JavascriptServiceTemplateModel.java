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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.ui.common.validation.ValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class JavascriptServiceTemplateModel extends GenerationModel {

	private static final String ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_GENERATION = Messages.JavascriptServiceTemplateModel_ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_GENERATION;

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.JavascriptServiceTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

	private static final Logger logger = Logger.getLogger(JavascriptServiceTemplateModel.class);

	private String tableName;

	private String tableType;

	private TableColumn[] tableColumns;

	private boolean columnsInit = false;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public TableColumn[] getTableColumns() {
		if (!columnsInit) {
			createTableColumns();
		}
		return tableColumns; // NOPMD
	}

	public void setTableColumns(TableColumn[] tableColumns) {
		this.tableColumns = tableColumns;
		columnsInit = true;
	}

	@Override
	public IValidationStatus validate() {
		IValidationStatus locationStatus = validateLocation();
		if (locationStatus.hasErrors()) {
			return locationStatus;
		}
		IValidationStatus templateStatus = validateTemplate();
		if (locationStatus.hasErrors()) {
			return locationStatus;
		}
		// if (!validateTableName()) {
		// return false;
		// }
		return ValidationStatus.getValidationStatus(locationStatus, templateStatus);
	}

	public IValidationStatus validateLocation() {
		IValidationStatus status;
		try {
			status = validateLocationGeneric();
			if (status.hasErrors()) {
				return status;
			}
			IPath location = new Path(getTargetLocation()).append(getFileName());
			// TODO - more precise test for the location ../WebContent/...
			if (location.toString().indexOf(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES) == -1) {
				return ValidationStatus.createError(TARGET_LOCATION_IS_NOT_ALLOWED);
			}
		} catch (Exception e) {
			// temp workaround due to another bug - context menu is not context
			// aware => target location and name are null (in the first page of
			// the wizard)
			return ValidationStatus.createError(""); //$NON-NLS-1$
		}
		return status;
	}

	public boolean validateTableName() {
		return tableName != null;
	}

	private void createTableColumns() {

		if (getTableName() == null) {
			return;
		}

		try {

			Connection connection = null;
			try {
				connection = DataSourceFacade.getInstance().getDataSource(CommonParameters.getRequest()).getConnection();

				List<TableColumn> availableTableColumns = new ArrayList<TableColumn>();

				ResultSet primaryKeys = DBUtils.getPrimaryKeys(connection, getTableName());
				while (primaryKeys.next()) {
					String columnName = primaryKeys.getString("COLUMN_NAME"); //$NON-NLS-1$
					TableColumn tableColumn = new TableColumn(columnName, 0, true, true);
					availableTableColumns.add(tableColumn);
				}

				ResultSet columns = DBUtils.getColumns(connection, getTableName());
				while (columns.next()) {
					// columns
					String columnName = columns.getString("COLUMN_NAME"); //$NON-NLS-1$
					int columnType = columns.getInt("DATA_TYPE"); //$NON-NLS-1$

					TableColumn tableColumn = new TableColumn(columnName, columnType, false, true);
					if (!exists(availableTableColumns, tableColumn)) {
						availableTableColumns.add(tableColumn);
					}
				}

				setTableColumns(availableTableColumns.toArray(new TableColumn[] {}));
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
		if (getTableName() == null) {
			return false;
		}
		for (TableColumn tableColumn2 : availableTableColumns) {
			TableColumn tableColumnX = tableColumn2;
			if (tableColumnX.getName().equals(tableColumn.getName())) {
				tableColumnX.setType(tableColumn.getType());
				return true;
			}
		}
		return false;
	}
}
