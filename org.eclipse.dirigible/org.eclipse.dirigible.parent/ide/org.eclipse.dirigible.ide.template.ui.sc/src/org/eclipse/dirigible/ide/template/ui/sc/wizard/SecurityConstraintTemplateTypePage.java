/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.sc.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeWizardPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class SecurityConstraintTemplateTypePage extends TemplateTypeWizardPage {

	private static final String MAIN_ACCESS_FILE = Messages.SecurityConstraintTemplateTypePage_MAIN_ACCESS_FILE;

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.SecurityConstraintTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.SecurityConstraintTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;

	private static final long serialVersionUID = -1269424557332755529L;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.sc.wizard.SecurityConstraintTemplateTypePage"; //$NON-NLS-1$

	private SecurityConstraintTemplateModel model;

	protected SecurityConstraintTemplateTypePage(SecurityConstraintTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	protected String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_SECURITY_CONSTRAINTS;
	}

}
