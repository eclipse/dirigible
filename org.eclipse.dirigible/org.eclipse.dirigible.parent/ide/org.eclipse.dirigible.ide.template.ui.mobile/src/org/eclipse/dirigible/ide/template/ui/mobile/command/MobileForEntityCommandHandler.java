package org.eclipse.dirigible.ide.template.ui.mobile.command;

import org.eclipse.core.resources.IFile;
import org.eclipse.dirigible.ide.template.ui.common.TemplateFileCommandHandler;
import org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileApplicationForEntityTemplateWizard;
import org.eclipse.jface.wizard.Wizard;

public class MobileForEntityCommandHandler extends TemplateFileCommandHandler {

	@Override
	protected Wizard getWizard(IFile file) {
		return new MobileApplicationForEntityTemplateWizard(file);
	}

}