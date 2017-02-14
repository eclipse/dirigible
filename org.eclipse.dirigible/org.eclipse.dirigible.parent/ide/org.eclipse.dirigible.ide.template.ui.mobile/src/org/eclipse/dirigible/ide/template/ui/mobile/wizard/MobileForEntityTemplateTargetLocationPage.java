package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

public class MobileForEntityTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileForEntityTemplateTargetLocationPage"; //$NON-NLS-1$
	private static final String TARGET_LOCATION = Messages.MobileTemplateTargetLocationPage_TARGET_LOCATION;
	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.MobileTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String DEFAULT_FILE_NAME = "app_name.js"; //$NON-NLS-1$
	private static final String FILE_EXTENSION = ".js"; //$NON-NLS-1$

	private MobileForEntityTemplateModel model;

	public MobileForEntityTemplateTargetLocationPage(MobileForEntityTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(TARGET_LOCATION);
		setDescription(SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME);
	}

	@Override
	protected GenerationModel getModel() {
		return this.model;
	}

	@Override
	protected String getDefaultFileName(String preset) {
		if ((getModel().getFileName() == null) || "".equals(getModel().getFileName())) { //$NON-NLS-1$
			return DEFAULT_FILE_NAME;
		}
		return (preset == null) ? getModel().getFileName()
				: CommonUtils.getFileNameNoExtension(preset) + FILE_EXTENSION;
	}

	@Override
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;
	}
}
