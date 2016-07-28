/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

public class DataStructureTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "DATA_STRUCTURE_GENERATOR"; //$NON-NLS-1$

	private DataStructureTemplateModel model;

	public DataStructureTemplateGenerator(DataStructureTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String fileNameNoExtension = model.getFileNameNoExtension();
		parameters.put("fileNameNoExtension", fileNameNoExtension.toUpperCase()); //$NON-NLS-1$
		parameters.put("columnDefinitions", model.getColumnDefinitions()); //$NON-NLS-1$
		parameters.put("query", model.getQuery()); //$NON-NLS-1$
		parameters.put("dsvSamples", model.getDsvSampleRows()); //$NON-NLS-1$
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

}
