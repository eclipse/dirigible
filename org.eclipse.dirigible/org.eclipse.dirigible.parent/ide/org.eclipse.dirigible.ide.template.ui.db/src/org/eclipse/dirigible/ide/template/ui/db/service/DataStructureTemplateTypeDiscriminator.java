/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.service;

import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

/**
 * Class holding the discrimination parameters for the Data Structure templates
 */
public class DataStructureTemplateTypeDiscriminator implements TemplateTypeDiscriminator {

	/**
	 * Category of the template
	 *
	 * @return the category
	 */
	@Override
	public String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
	}

	/**
	 * Templates path within the Repository
	 *
	 * @return the templates path
	 */
	@Override
	public String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_DATA_STRUCTURES;
	}

}
