/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.db.wizard.ColumnDefinition;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateModel;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DatabaseGenerationWorker extends AbstractGenerationWorker {

	private static final String PARAM_COLUMN_DEFAULT_VALUE = "defaultValue";
	private static final String PARAM_COLUMN_NOT_NULL = "notNull";
	private static final String PARAM_COLUMN_PRIMARY_KEY = "primaryKey";
	private static final String PARAM_COLUMN_LENGTH = "length";
	private static final String PARAM_COLUMN_TYPE = "type";
	private static final String PARAM_COLUMN_NAME = "name";
	private static final String PARAM_ROWS = "rows";
	private static final String PARAM_QUERY = "query";
	private static final String PARAM_COLUMNS = "columns";

	private static final DataStructureTemplateModel model = new DataStructureTemplateModel();
	private static final DataStructureTemplateGenerator generator = new DataStructureTemplateGenerator(model);
	private static final TemplateTypeDiscriminator typeDiscriminator = new DataStructureTemplateTypeDiscriminator();

	public DatabaseGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected void readAndSetExtraParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates)
			throws GenerationException {
		DataStructureTemplateModel dbModel = (DataStructureTemplateModel) model;
		// columns
		if (parametersObject.has(PARAM_COLUMNS)) {
			List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
			JsonArray columns = parametersObject.get(PARAM_COLUMNS).getAsJsonArray();
			Iterator<JsonElement> iter = columns.iterator();
			while (iter.hasNext()) {
				JsonElement columnElement = iter.next();
				if (columnElement.isJsonObject()) {
					JsonObject columnObject = columnElement.getAsJsonObject();
					ColumnDefinition columnDefinition = new ColumnDefinition();
					columnDefinition.setName(columnObject.get(PARAM_COLUMN_NAME).getAsString());
					columnDefinition.setType(columnObject.get(PARAM_COLUMN_TYPE).getAsString());
					columnDefinition.setLength(columnObject.get(PARAM_COLUMN_LENGTH).getAsInt());
					columnDefinition.setPrimaryKey(columnObject.get(PARAM_COLUMN_PRIMARY_KEY).getAsBoolean());
					columnDefinition.setNotNull(columnObject.get(PARAM_COLUMN_NOT_NULL).getAsBoolean());
					columnDefinition.setDefaultValue(columnObject.get(PARAM_COLUMN_DEFAULT_VALUE).getAsString());
					columnDefinitions.add(columnDefinition);
				}
			}

			dbModel.setColumnDefinitions(columnDefinitions.toArray(new ColumnDefinition[] {}));
		} else {
			checkIfRequired(dbModel, PARAM_COLUMNS);
		}
		// query
		if (parametersObject.has(PARAM_QUERY)) {
			dbModel.setQuery(parametersObject.get(PARAM_QUERY).getAsString());
		} else {
			checkIfRequired(dbModel, PARAM_QUERY);
		}

		// query
		if (parametersObject.has(PARAM_ROWS)) {
			String rows = parametersObject.get(PARAM_ROWS).getAsString();
			dbModel.setDsvSampleRows(rows.split((rows.indexOf("\n") >= 0) ? "\n" : "\r"));
		} else {
			checkIfRequired(dbModel, PARAM_ROWS);
		}
	}

	@Override
	protected GenerationModel getTemplateModel() {
		return this.model;
	}

	@Override
	protected TemplateGenerator getTemplateGenerator() {
		return this.generator;
	}

	@Override
	protected TemplateTypeDiscriminator getTypeDiscriminator() {
		return this.typeDiscriminator;
	}

}
