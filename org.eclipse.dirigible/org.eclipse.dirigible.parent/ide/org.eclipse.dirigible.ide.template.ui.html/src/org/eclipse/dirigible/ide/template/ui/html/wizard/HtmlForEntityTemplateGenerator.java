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

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class HtmlForEntityTemplateGenerator extends TemplateGenerator {

//	private static final String REST_SERVICE_ROOT_JS = "/dirigible/services/js"; //$NON-NLS-1$
	private static final String REST_SERVICE_ROOT_JS = "../../js"; //$NON-NLS-1$

	private static final String LOG_TAG = "HTML_FOR_ENTITY_GENERATOR"; //$NON-NLS-1$

	private HtmlForEntityTemplateModel model;

	public HtmlForEntityTemplateGenerator(HtmlForEntityTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pageTitle", model.getPageTitle()); //$NON-NLS-1$
		parameters.put("tableName", model.getTableName()); //$NON-NLS-1$
		parameters.put("tableColumns", model.getTableColumns()); //$NON-NLS-1$
		parameters.put("fileName", model.getFileName()); //$NON-NLS-1$
		parameters.put("serviceFileName", generateServiceFileName()); //$NON-NLS-1$
		parameters.put("createDataModel", createDataModel()); //$NON-NLS-1$
		parameters.put("entityName", getEntityName()); //$NON-NLS-1$
		// parameters.put("projectName", model.getProjectName());
		return parameters;
	}

	protected String getEntityName() {
		return "data"; //$NON-NLS-1$
	}

	protected String createDataModel() {
		return "tableModel = new sap.ui.model.json.JSONModel();\n		tableModel.loadData(\"" //$NON-NLS-1$
				+ generateServiceFileName() + "\");"; //$NON-NLS-1$
	}

	protected Object generateServiceFileName() {
		// /project1/ScriptingServices/te1.entity
		IFile source = model.getSourceFile();
		String entityPath = source.getFullPath().toString();
		String result = "";
		int index = entityPath.indexOf(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES);
		if (index >= 0) {
			result = entityPath.substring(index + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES.length());
		} else {
			result = entityPath; //$NON-NLS-1$
		}
		result = result.replace(source.getFileExtension(), ""); //$NON-NLS-1$
		result += "js"; //$NON-NLS-1$
		return REST_SERVICE_ROOT_JS + result;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

}
