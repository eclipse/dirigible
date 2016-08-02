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

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeWizardPage;
import org.eclipse.dirigible.ide.template.ui.db.service.DataStructureTemplateTypeDiscriminator;

/**
 * The wizard page for choosing the template type
 */
public class DataStructureTemplateTypePage extends TemplateTypeWizardPage {

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.DataStructureTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.DataStructureTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateTypePage"; //$NON-NLS-1$

	private TableTemplateModel model;

	protected DataStructureTemplateTypePage(TableTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getCategory() {
		return DataStructureTemplateTypeDiscriminator.getCategory();
	}

	@Override
	protected String getTemplatesPath() {
		return DataStructureTemplateTypeDiscriminator.getTemplatesPath();
	}

}
