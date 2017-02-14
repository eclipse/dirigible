package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class MobileForEntityTemplateModel extends ContentForEntityModel {

	@Override
	protected String getArtifactType() {
		return ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;
	}

}
