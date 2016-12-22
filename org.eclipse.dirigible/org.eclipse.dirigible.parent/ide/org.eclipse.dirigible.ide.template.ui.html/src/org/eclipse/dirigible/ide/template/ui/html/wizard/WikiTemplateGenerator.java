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

import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

/**
 * Wiki Template Generator
 */
public class WikiTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "WIKI_GENERATOR"; //$NON-NLS-1$

	private static final String PARAMETER_PAGE_TITLE = "pageTitle";//$NON-NLS-1$

	private WikiTemplateModel model;

	/**
	 * Constructor
	 *
	 * @param model
	 */
	public WikiTemplateGenerator(WikiTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_PAGE_TITLE, model.getPageTitle());
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
