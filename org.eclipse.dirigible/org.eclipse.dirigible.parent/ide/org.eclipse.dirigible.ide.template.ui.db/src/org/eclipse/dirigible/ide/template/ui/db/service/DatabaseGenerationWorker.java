package org.eclipse.dirigible.ide.template.ui.db.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.IGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypesEnumerator;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateModel;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateTypeDiscriminator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DatabaseGenerationWorker implements IGenerationWorker {

	@Override
	public void generate(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			JsonElement parametersElement = new JsonParser().parse(parameters);
			JsonObject parametersObject = parametersElement.getAsJsonObject();

			DataStructureTemplateModel model = new DataStructureTemplateModel();
			DataStructureTemplateGenerator generator = new DataStructureTemplateGenerator(model);

			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(DataStructureTemplateTypeDiscriminator.getTemplatesPath(),
					DataStructureTemplateTypeDiscriminator.getCategory(), request);

			setParametersToModel(parametersObject, model, templates);

			generator.generate();

		} catch (Exception e) {
			throw new GenerationException(e);
		}

	}

	protected void setParametersToModel(JsonObject parametersObject, DataStructureTemplateModel model, TemplateType[] templates)
			throws GenerationException {
		// table name
		if (parametersObject.has("tableName")) {
			model.setTableName(parametersObject.get("tableName").getAsString());
		} else {
			throw new GenerationException(String.format("Parameter %s has not been provided", "tableName"));
		}

		// template type
		if (parametersObject.has("templateType")) {
			String templateType = parametersObject.get("templateType").getAsString();
			for (TemplateType template : templates) {
				if (template.getLocation().substring(DataStructureTemplateTypeDiscriminator.getTemplatesPath().length()).indexOf(templateType) >= 0) {
					model.setTemplate(template);
					break;
				}

				if (model.getTemplate() == null) {
					throw new GenerationException(String.format("Template %s does not exist in this instance", templateType));
				}
			}
		} else {
			throw new GenerationException(String.format("Parameter %s has not been provided", "templateType"));
		}

		// file name
		if (parametersObject.has("fileName")) {
			model.setFileName(parametersObject.get("fileName").getAsString());
		} else {
			throw new GenerationException(String.format("Parameter %s has not been provided", "fileName"));
		}

		// package name
		if (parametersObject.has("packageName")) {
			model.setPackageName(parametersObject.get("packageName").getAsString());
		} else {
			throw new GenerationException(String.format("Parameter %s has not been provided", "packageName"));
		}

		// query
		if (parametersObject.has("query")) {
			model.setQuery(parametersObject.get("query").getAsString());
		} else {
			throw new GenerationException(String.format("Parameter %s has not been provided", "query"));
		}
	}

}
