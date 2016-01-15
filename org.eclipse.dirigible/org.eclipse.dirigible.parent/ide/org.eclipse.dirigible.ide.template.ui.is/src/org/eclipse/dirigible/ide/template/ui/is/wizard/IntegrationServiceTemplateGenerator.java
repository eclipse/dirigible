/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.is.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

public class IntegrationServiceTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "INTEGRATION_SERVICE_GENERATOR"; //$NON-NLS-1$

	private IntegrationServiceTemplateModel model;

	public IntegrationServiceTemplateGenerator(IntegrationServiceTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", model.getId()); //$NON-NLS-1$
		parameters.put("endpointAddress", model.getEndpointAddress()); //$NON-NLS-1$
		// parameters.put("parameterName", model.getParameterName()); //$NON-NLS-1$
		parameters.put("projectName", model.getProjectName()); //$NON-NLS-1$
		String fileNameNoExtension = model.getFileNameNoExtension();
		parameters.put("fileNameNoExtension", fileNameNoExtension); //$NON-NLS-1$
		String fileNameNoExtensionTitle = fileNameNoExtension;
		if ((fileNameNoExtension != null) && (fileNameNoExtension.length() > 1)) {
			char[] chars = fileNameNoExtension.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			fileNameNoExtensionTitle = new String(chars);
		}
		parameters.put("fileNameNoExtensionTitle", fileNameNoExtensionTitle); //$NON-NLS-1$
		// parameters.put("originalEndpoint", model.getOriginalEndpoint()); //$NON-NLS-1$
		return parameters;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

	@Override
	protected byte[] afterGeneration(byte[] bytes) {
		String content = new String(bytes);
		content = content.replace("\\$", "$"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\{", "{"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\}", "}"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
		byte[] result = content.getBytes();
		return result;
	}

	@Override
	public void generate() throws Exception {
		super.generate();
		// IPath targetLocationPath = new Path(getModel().getTargetLocation());
		// String projectName = getModel().getProjectName();
		// targetLocationPath = targetLocationPath.removeLastSegments(targetLocationPath.segmentCount() - 1);
		// String targetLocationPathJavaScriptServices = targetLocationPath.append(IRepository.SEPARATOR)
		// .append(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES).append(IRepository.SEPARATOR).append(projectName).toString();
		//
		// if ("/org/eclipse/dirigible/ide/template/ui/is/templates/shielding-js.flow" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		//
		// generateFile("/org/eclipse/dirigible/ide/template/ui/is/templates/javascript-sync.js", //$NON-NLS-1$
		// targetLocationPathJavaScriptServices, model.getEndpointAddress() +
		// ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT);
		// }
		//
		// if ("/org/eclipse/dirigible/ide/template/ui/is/templates/trigger-to-javascript.job" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		//
		// generateFile("/org/eclipse/dirigible/ide/template/ui/is/templates/javascript-async.js", //$NON-NLS-1$
		// targetLocationPathJavaScriptServices, model.getEndpointAddress() +
		// ICommonConstants.ARTIFACT_EXTENSION.JAVASCRIPT);
		// }

	}

}
