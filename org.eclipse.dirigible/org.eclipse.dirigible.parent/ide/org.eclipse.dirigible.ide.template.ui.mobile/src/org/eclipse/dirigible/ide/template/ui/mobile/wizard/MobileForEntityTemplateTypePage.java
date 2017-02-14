package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class MobileForEntityTemplateTypePage extends MobileTemplateTypePage {

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.MobileTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;
	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.MobileTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;
	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileForEntityTemplateTypePage"; //$NON-NLS-1$
	private MobileForEntityTemplateModel model;

	protected MobileForEntityTemplateTypePage(MobileForEntityTemplateModel model) {
		super(null, PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

	@Override
	protected GenerationModel getModel() {
		return this.model;
	}

	@Override
	protected String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_MOBILE_APP_FOR_ENTITY;
	}
}
