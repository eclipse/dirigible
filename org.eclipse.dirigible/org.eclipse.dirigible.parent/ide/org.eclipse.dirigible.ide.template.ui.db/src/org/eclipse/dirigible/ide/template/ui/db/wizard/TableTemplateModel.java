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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.datasource.DataSourceFacade;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.ui.common.validation.ValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class TableTemplateModel extends GenerationModel {

	private static final String TABLE_OR_VIEW_WITH_THE_SAME_NAME_ALREADY_EXISTS = Messages.TableTemplateModel_TABLE_OR_VIEW_WITH_THE_SAME_NAME_ALREADY_EXISTS;

	private static final Logger logger = Logger
			.getLogger(TableTemplateModel.class);

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.TableTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;
	private static final String NO_PRIMARY_KEY_FOUND = Messages.TableTemplateModel_NO_PRIMARY_KEY_FOUND;
	private static final String DUPLICATE_COLUMN_NAMES_FOUND = Messages.TableTemplateModel_DUPLICATE_COLUMN_NAMES_FOUND;
	private static final String NO_COLUMNS_DEFINED = Messages.TableTemplateModel_NO_COLUMNS_DEFINED;
	private ColumnDefinition[] columnDefinitions;

	public TableTemplateModel() {
		super();
	}

	public ColumnDefinition[] getColumnDefinitions() {
		return columnDefinitions; // NOPMD
	}

	public void setColumnDefinitions(ColumnDefinition[] columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
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

		return ValidationStatus.getValidationStatus(locationStatus,
				templateStatus);
	}

	public IValidationStatus validateLocation() {
		IValidationStatus status;
		try {
			status = validateLocationGeneric();
			if (status.hasErrors()) {
				return status;
			}
			IPath location = new Path(getTargetLocation())
			.append(getFileName());
			// TODO - more precise test for the location ../DataStructures/...
			if (location.toString().indexOf(
					ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES) == -1) {
				return ValidationStatus
						.createError(TARGET_LOCATION_IS_NOT_ALLOWED);
			}

			// check the file name against the existing table names
			if (isTableExists(getFileNameNoExtension().toLowerCase())
					|| (isTableExists(getFileNameNoExtension().toUpperCase()) && !(getFileName()
							.endsWith(".dsv")))) {
				return ValidationStatus
						.createError(TABLE_OR_VIEW_WITH_THE_SAME_NAME_ALREADY_EXISTS);
			}

		} catch (Exception e) {
			// temp workaround due to another bug - context menu is not context
			// aware => target location and name are null (in the first page of
			// the wizard)
			return ValidationStatus.createError(""); //$NON-NLS-1$
		}
		return status;
	}

	private boolean isTableExists(String tableName) {
		Connection connection = null;
		try {
			DataSource dataSource = DataSourceFacade.getInstance()
					.getDataSource();
			connection = dataSource.getConnection();

			return DBUtils.isTableOrViewExists(connection, tableName);

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return false;
	}

	public IValidationStatus validateColumnDefinitions() {
		if (columnDefinitions.length <= 0) {
			return ValidationStatus.createError(NO_COLUMNS_DEFINED);
		}
		if (duplicateNames(columnDefinitions)) {
			return ValidationStatus.createError(DUPLICATE_COLUMN_NAMES_FOUND);
		}
		if (!thereIsPrimaryKey()) {
			return ValidationStatus.createError(NO_PRIMARY_KEY_FOUND);
		}
		return ValidationStatus.createOk();
	}

	private boolean duplicateNames(ColumnDefinition[] columnDefinitions) {
		Set<String> temp = new HashSet<String>();
		for (ColumnDefinition columnDefinition : columnDefinitions) {
			if (temp.contains(columnDefinition.getName())) {
				return true;
			}
			temp.add(columnDefinition.getName());
		}
		return false;
	}

	private boolean thereIsPrimaryKey() {
		for (ColumnDefinition columnDefinition : columnDefinitions) {
			if (columnDefinition.isPrimaryKey()) {
				return true;
			}
		}
		return false;
	}

}