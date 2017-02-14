package org.eclipse.dirigible.ide.template.ui.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.dirigible.ide.template.ui.common.table.TableColumn;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class ContentForEntityGenerationWorker extends AbstractGenerationWorker {

	private static final String PARAM_SERVICE_ENDPOINT = "serviceEndpoint";
	private static final String PARAM_PAGE_TITLE = "pageTitle";
	private static final String PARAM_COLUMN_LABEL = "label";
	private static final String PARAM_COLUMN_WIDGET_TYPE = "widgetType";
	private static final String PARAM_COLUMN_SIZE = "size";
	private static final String PARAM_COLUMN_VISIBLE = "visible";
	private static final String PARAM_COLUMN_PRIMARY_KEY = "primaryKey";
	private static final String PARAM_COLUMN_TYPE = "type";
	private static final String PARAM_COLUMN_NAME = "name";
	private static final String PARAM_TABLE_NAME = "tableName";
	private static final String PARAM_DEPENDENT_COLUMN = "dependentColumn";
	private static final String PARAM_COLUMNS = "columns";

	public ContentForEntityGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected void readAndSetExtraParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates)
			throws GenerationException {
		ContentForEntityModel contentModel = (ContentForEntityModel) model;
		// columns
		if (parametersObject.has(PARAM_COLUMNS)) {
			List<TableColumn> columnDefinitions = new ArrayList<TableColumn>();
			JsonArray columns = parametersObject.get(PARAM_COLUMNS).getAsJsonArray();
			Iterator<JsonElement> iter = columns.iterator();
			while (iter.hasNext()) {
				JsonElement columnElement = iter.next();
				if (columnElement.isJsonObject()) {
					JsonObject columnObject = columnElement.getAsJsonObject();
					TableColumn columnDefinition = new TableColumn(columnObject.get(PARAM_COLUMN_NAME).getAsString(),
							columnObject.get(PARAM_COLUMN_PRIMARY_KEY).getAsBoolean(), columnObject.get(PARAM_COLUMN_VISIBLE).getAsBoolean(),
							columnObject.get(PARAM_COLUMN_TYPE).getAsString(), columnObject.get(PARAM_COLUMN_SIZE).getAsInt(),
							columnObject.get(PARAM_COLUMN_WIDGET_TYPE).getAsString(), columnObject.get(PARAM_COLUMN_LABEL).getAsString());
					columnDefinitions.add(columnDefinition);
				}
			}

			contentModel.setTableColumns(columnDefinitions.toArray(new TableColumn[] {}));
		} else {
			checkIfRequired(contentModel, PARAM_COLUMNS);
		}

		// table name
		if (parametersObject.has(PARAM_TABLE_NAME)) {
			contentModel.setTableName(parametersObject.get(PARAM_TABLE_NAME).getAsString());
		} else {
			checkIfRequired(contentModel, PARAM_TABLE_NAME);
		}

		// dependent column
		if (parametersObject.has(PARAM_DEPENDENT_COLUMN)) {
			contentModel.setDependentColumn(parametersObject.get(PARAM_DEPENDENT_COLUMN).getAsString());
		} else {
			checkIfRequired(contentModel, PARAM_DEPENDENT_COLUMN);
		}

		// page title
		if (parametersObject.has(PARAM_PAGE_TITLE)) {
			contentModel.setPageTitle(parametersObject.get(PARAM_PAGE_TITLE).getAsString());
		} else {
			checkIfRequired(contentModel, PARAM_PAGE_TITLE);
		}

		// service endpoint
		if (parametersObject.has(PARAM_SERVICE_ENDPOINT)) {
			contentModel.setServiceEndpoint(parametersObject.get(PARAM_SERVICE_ENDPOINT).getAsString());
		} else {
			checkIfRequired(contentModel, PARAM_SERVICE_ENDPOINT);
		}
	}
}
