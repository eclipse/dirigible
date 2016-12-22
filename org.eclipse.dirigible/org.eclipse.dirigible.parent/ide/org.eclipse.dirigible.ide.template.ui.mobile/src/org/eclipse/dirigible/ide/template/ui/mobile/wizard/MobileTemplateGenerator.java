/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

/**
 * Mobile template generator
 */
public class MobileTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "MOBILE_GENERATOR"; //$NON-NLS-1$
	private static final String PARAMETER_FILE_NAME = "fileName"; //$NON-NLS-1$

	private MobileTemplateModel model;

	/**
	 * Constructor
	 *
	 * @param model
	 */
	public MobileTemplateGenerator(MobileTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_FILE_NAME, model.getFileName());
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
}
