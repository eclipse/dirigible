package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class MobileApplicationForEntityTemplateWizard extends TemplateWizard {

	private static final String CREATE_USER_INTERFACE_FOR_RES_TFUL_PERSISTENCE_SERVICE = Messages.MobileForEntityTemplateWizard_CREATE_USER_MOBILE_FOR_RES_TFUL_PERSISTENCE_SERVICE;
	private final MobileForEntityTemplateModel model;
	private final MobileTemplateTypePage typesPage;
	private final MobileForEntityTemplateTitlePage titlePage;
	private final MobileForEntityTemplateTablePage tablePage;
	private final MobileForEntityTemplateTargetLocationPage targetLocationPage;

	public MobileApplicationForEntityTemplateWizard(IFile file) {
		setWindowTitle(CREATE_USER_INTERFACE_FOR_RES_TFUL_PERSISTENCE_SERVICE);
		model = new MobileForEntityTemplateModel();
		model.setSourceResource(file);
		typesPage = new MobileForEntityTemplateTypePage(model);
		titlePage = new MobileForEntityTemplateTitlePage(model);
		tablePage = new MobileForEntityTemplateTablePage(model);
		targetLocationPage = new MobileForEntityTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(tablePage);
		addPage(titlePage);
		addPage(targetLocationPage);
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		return new MobileForEntityTemplateGenerator(model);
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(
					String.format(StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED, model.getFileName()));
		}
		return result;
	}
}
