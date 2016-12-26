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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.ui.common.validation.ValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class HtmlForEntityTemplateModel extends GenerationModel {

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.HtmlForEntityTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

	private String tableName;

	private String tableType;

	private String dependentColumn;

	private TableColumn[] tableColumns;

	private String pageTitle;

	private String serviceEndpoint;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableType() {
		return tableType;
	}

	public String getDependentColumn() {
		return dependentColumn;
	}

	public void setDependentColumn(String dependentColumn) {
		this.dependentColumn = dependentColumn;
	}

	public TableColumn[] getOriginalTableColumns() {
		return this.tableColumns; // NOPMD
	}

	public TableColumn[] getTableColumns() {
		TableColumn[] originalCased = this.tableColumns;
		TableColumn[] lowerCased = new TableColumn[originalCased.length];
		for (int i = 0; i < originalCased.length; i++) {
			TableColumn originalColumn = originalCased[i];
			TableColumn lowerColumn = new TableColumn(originalColumn.getName().toLowerCase(), originalColumn.isKey(), originalColumn.isVisible(),
					originalColumn.getType(), originalColumn.getSize(), originalColumn.getWidgetType(), originalColumn.getLabel());
			lowerCased[i] = lowerColumn;
		}
		return lowerCased;
	}

	public void setTableColumns(TableColumn[] tableColumns) {
		this.tableColumns = tableColumns;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
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

		return ValidationStatus.getValidationStatus(locationStatus, templateStatus);
	}

	@Override
	public IValidationStatus validateLocation() {
		IValidationStatus status;
		try {
			status = validateLocationGeneric();
			if (status.hasErrors()) {
				return status;
			}
			IPath location = new Path(getTargetLocation()).append(getFileName());
			// TODO - more precise test for the location ../WebContent/...
			if (location.toString().indexOf(ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT) == -1) {
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

	public boolean validateTableColumns() {
		for (TableColumn tableColumn : tableColumns) {
			if (tableColumn.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public IFile getSourceFile() {
		IResource resource = getSourceResource();
		if (resource instanceof IFile) {
			return (IFile) resource;
		}
		return null;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

}
