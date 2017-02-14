package org.eclipse.dirigible.ide.template.ui.mobile.service;

import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class MobileForEntityTemplateTypeDiscriminator implements TemplateTypeDiscriminator {
	/**
	 * Category of the template
	 *
	 * @return the category
	 */
	public String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;
	}

	/**
	 * Templates path within the Repository
	 *
	 * @return the templates path
	 */
	public String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_MOBILE_APP_FOR_ENTITY;
	}
}
