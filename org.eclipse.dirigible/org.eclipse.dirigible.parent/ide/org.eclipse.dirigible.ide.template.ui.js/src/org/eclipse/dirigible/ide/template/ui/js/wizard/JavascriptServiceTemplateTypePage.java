/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeWizardPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class JavascriptServiceTemplateTypePage extends TemplateTypeWizardPage {

	private static final long serialVersionUID = -9193812975192988551L;

	private static final String RUBY_SERVICE = Messages.JavascriptServiceTemplateTypePage_RUBY_SERVICE;

	private static final String GROOVY_SERVICE = Messages.JavascriptServiceTemplateTypePage_GROOVY_SERVICE;

	private static final String ENTITY_SERVICE_ON_TABLE = Messages.JavascriptServiceTemplateTypePage_ENTITY_SERVICE_ON_TABLE;

	private static final String DATABASE_ACCESS_SAMPLE = Messages.JavascriptServiceTemplateTypePage_DATABASE_ACCESS_SAMPLE;

	private static final String SERVER_SIDE_JAVA_SCRIPT_GUID_GENERATOR_LIBRARY = Messages.JavascriptServiceTemplateTypePage_SERVER_SIDE_JAVA_SCRIPT_GUID_GENERATOR_LIBRARY;

	private static final String BLANK_SERVER_SIDE_JAVA_SCRIPT_SERVICE = Messages.JavascriptServiceTemplateTypePage_BLANK_SERVER_SIDE_JAVA_SCRIPT_SERVICE;

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.JavascriptServiceTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.JavascriptServiceTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.js.wizard.JavascriptServiceTemplateTypePage"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	protected JavascriptServiceTemplateTypePage(JavascriptServiceTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	protected String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_SCRIPTING_SERVICES;
	}

}
