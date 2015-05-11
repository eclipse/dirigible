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

package org.eclipse.dirigible.ide.template.ui.ed.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeWizardPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class ExtensionDefinitionTemplateTypePage extends TemplateTypeWizardPage {

	private static final String MAIN_ACCESS_FILE = Messages.ExtensionDefinitionTemplateTypePage_MAIN_ACCESS_FILE;

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.ExtensionDefinitionTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.ExtensionDefinitionTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;

	private static final long serialVersionUID = -1269424557332755529L;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.sc.wizard.ExtensionDefinitionTemplateTypePage"; //$NON-NLS-1$

	private ExtensionDefinitionTemplateModel model;

	protected ExtensionDefinitionTemplateTypePage(
			ExtensionDefinitionTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	protected String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

}
