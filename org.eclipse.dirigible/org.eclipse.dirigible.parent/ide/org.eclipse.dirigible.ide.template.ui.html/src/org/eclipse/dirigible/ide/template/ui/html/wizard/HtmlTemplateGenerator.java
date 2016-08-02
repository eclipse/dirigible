/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

public class HtmlTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "HTML_GENERATOR"; //$NON-NLS-1$

	private HtmlTemplateModel model;

	public HtmlTemplateGenerator(HtmlTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pageTitle", model.getPageTitle()); //$NON-NLS-1$
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
	public void generate() throws Exception {
		super.generate();
	}

	@Override
	protected byte[] afterGeneration(byte[] bytes) {
		byte[] result = model.normalizeEscapes(bytes);
		return result;
	}

}
