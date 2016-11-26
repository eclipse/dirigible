package org.eclipse.dirigible.ide.workspace.ui.view;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;

public class SwaggerView extends WebViewerView {

	private static final String SWAGGER_UI = "/web/swagger_ui/index.html?url="; //$NON-NLS-1$

	private static final String SWAGGER_EXTENSION = ".swagger"; //$NON-NLS-1$

	@Override
	protected String updatePath(String text) {
		if ((text != null) && text.endsWith(SWAGGER_EXTENSION)) {
			return CommonIDEParameters.getServicesUrl() + SWAGGER_UI + text;
		}
		return null;
	}

}
