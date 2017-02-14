package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.dirigible.ide.template.ui.common.WizardForEntityTamplateTablePage;
import org.eclipse.jface.viewers.IBaseLabelProvider;

public class MobileForEntityTemplateTablePage extends WizardForEntityTamplateTablePage {

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileForEntityTemplateTablePage"; //$NON-NLS-1$

	protected MobileForEntityTemplateTablePage(MobileForEntityTemplateModel model) {
		super(model, PAGE_NAME);
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new MobileForEntityTemplateTablePageLabelProvider();
	}
}
