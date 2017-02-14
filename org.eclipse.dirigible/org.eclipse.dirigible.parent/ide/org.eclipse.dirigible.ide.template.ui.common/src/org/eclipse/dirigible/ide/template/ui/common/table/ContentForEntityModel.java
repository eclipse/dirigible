package org.eclipse.dirigible.ide.template.ui.common.table;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.Messages;

public abstract class ContentForEntityModel extends GenerationModel {

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.WizardForEntityTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

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

	@Override
	protected String getTargetLocationErrorMessage() {
		return TARGET_LOCATION_IS_NOT_ALLOWED;
	}
}
