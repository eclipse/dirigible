package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class HtmlForEntityTemplateModel extends ContentForEntityModel {

	@Override
	protected String getArtifactType() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}
}
