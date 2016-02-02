/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.ed.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateUtils;

public class ExtensionDefinitionTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "SECURITY_CONSTRAINT_GENERATOR"; //$NON-NLS-1$

	private ExtensionDefinitionTemplateModel model;

	public ExtensionDefinitionTemplateGenerator(ExtensionDefinitionTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fileNameNoExtension", model.getFileNameNoExtension()); //$NON-NLS-1$
		parameters.put("projectName", model.getProjectName()); //$NON-NLS-1$

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
		byte[] result = TemplateUtils.normalizeEscapes(bytes);
		return result;
	}
}
