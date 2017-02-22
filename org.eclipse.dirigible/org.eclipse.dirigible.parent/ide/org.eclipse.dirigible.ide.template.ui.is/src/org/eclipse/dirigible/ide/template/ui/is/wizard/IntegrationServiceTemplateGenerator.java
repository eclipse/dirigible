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

import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.repository.api.ICommonConstants;

/**
 * IntegrationService Template Generator
 */
public class IntegrationServiceTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "INTEGRATION_SERVICE_GENERATOR"; //$NON-NLS-1$

	private static final String PARAMETER_ID = "id"; //$NON-NLS-1$
	private static final String PARAMETER_ENDPOINT_ADDRESS = "endpointAddress"; //$NON-NLS-1$
	private static final String PARAMETER_FILE_NAME_NO_EXTENSION_TITLE = "fileNameNoExtensionTitle"; //$NON-NLS-1$

	private IntegrationServiceTemplateModel model;

	/**
	 * Constructor
	 *
	 * @param model
	 */
	public IntegrationServiceTemplateGenerator(IntegrationServiceTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_ID, model.getId());
		parameters.put(PARAMETER_ENDPOINT_ADDRESS, model.getEndpointAddress());
		String fileNameNoExtension = model.getFileNameNoExtension();
		String fileNameNoExtensionTitle = fileNameNoExtension;
		if ((fileNameNoExtension != null) && (fileNameNoExtension.length() > 1)) {
			char[] chars = fileNameNoExtension.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			fileNameNoExtensionTitle = new String(chars);
		}
		parameters.put(PARAMETER_FILE_NAME_NO_EXTENSION_TITLE, fileNameNoExtensionTitle);
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
		return model.normalizeEscapes(bytes);
	}

	@Override
	protected String getDefaultRootFolder() {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

}
