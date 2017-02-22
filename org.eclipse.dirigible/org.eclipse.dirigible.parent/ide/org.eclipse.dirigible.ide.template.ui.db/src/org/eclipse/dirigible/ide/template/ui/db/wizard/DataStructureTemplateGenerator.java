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

import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.repository.api.ICommonConstants;

/**
 * DataStructure Template Generator
 */
public class DataStructureTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "DATA_STRUCTURE_GENERATOR"; //$NON-NLS-1$
	private static final String PARAMETER_COLUMN_DEFINITIONS = "columnDefinitions"; //$NON-NLS-1$
	private static final String PARAMETER_QUERY = "query"; //$NON-NLS-1$
	private static final String PARAMETER_DSV_SAMPLES = "dsvSamples"; //$NON-NLS-1$

	private DataStructureTemplateModel model;

	/**
	 * Constructor
	 *
	 * @param model
	 */
	public DataStructureTemplateGenerator(DataStructureTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_COLUMN_DEFINITIONS, model.getColumnDefinitions());
		parameters.put(PARAMETER_QUERY, model.getQuery());
		parameters.put(PARAMETER_DSV_SAMPLES, model.getDsvSampleRows());
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
	protected String getDefaultRootFolder() {
		return ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
	}

}
