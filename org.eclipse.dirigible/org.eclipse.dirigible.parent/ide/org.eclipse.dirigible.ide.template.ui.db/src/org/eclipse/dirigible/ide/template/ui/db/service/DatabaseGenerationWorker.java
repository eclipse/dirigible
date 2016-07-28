package org.eclipse.dirigible.ide.template.ui.db.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypesEnumerator;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateModel;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateTypeDiscriminator;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DatabaseGenerationWorker extends AbstractGenerationWorker {

	public DatabaseGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	public String generate(String parameters, HttpServletRequest request) throws GenerationException {
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
		return "Generation passed successfully.";
	}

	protected void setParametersToModel(JsonObject parametersObject, DataStructureTemplateModel model, TemplateType[] templates)
			throws GenerationException {

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
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "templateType"));
		}

		// file name
		if (parametersObject.has("fileName")) {
			model.setFileName(parametersObject.get("fileName").getAsString());
		} else {
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "fileName"));
		}

		// project name
		if (parametersObject.has("projectName")) {
			String projectName = parametersObject.get("projectName").getAsString();
			IProject project = getWorkspace().getRoot().getProject(projectName);
			if (project.exists()) {
				model.setTargetContainer(project.getFullPath().toString());
			}
		} else {
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "projectName"));
		}

		// package name
		if (parametersObject.has("packageName")) {
			model.setPackageName(parametersObject.get("packageName").getAsString());
		} else {
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "packageName"));
		}

		// table name
		if (parametersObject.has("tableName")) {
			model.setTableName(parametersObject.get("tableName").getAsString());
		} else {
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "tableName"));
		}

		// query
		if (parametersObject.has("query")) {
			model.setQuery(parametersObject.get("query").getAsString());
		} else {
			throw new GenerationException(String.format("Mandatory parameter %s has not been provided", "query"));
		}
	}

	@Override
	public String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(DataStructureTemplateTypeDiscriminator.getTemplatesPath(),
					DataStructureTemplateTypeDiscriminator.getCategory(), request);
			String result = new Gson().toJson(templates);
			return result;
		} catch (Exception e) {
			throw new GenerationException(e);
		}
	}

}
