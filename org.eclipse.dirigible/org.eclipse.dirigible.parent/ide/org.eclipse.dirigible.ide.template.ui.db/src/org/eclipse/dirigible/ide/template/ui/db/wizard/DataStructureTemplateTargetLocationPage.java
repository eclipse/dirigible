/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

public class DataStructureTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String VIEW_NAME_VIEW = "view_name.view";

	private static final String TABLE_NAME_TABLE = "table_name.table";

	private static final long serialVersionUID = -1678301320687605682L;

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.DataStructureTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String TARGET_LOCATION = Messages.DataStructureTemplateTargetLocationPage_TARGET_LOCATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateTargetLocationPage"; //$NON-NLS-1$

	private TableTemplateModel model;

	protected DataStructureTemplateTargetLocationPage(TableTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(TARGET_LOCATION);
		setDescription(SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME);
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getDefaultFileName(String preset) {
		String templateExt = model.getTemplateExtension();
		String defaultName = "noname.txt";
		if (templateExt.equals(DataStructureTemplateWizard.EXT_TABLE)) {
			defaultName = (preset == null) ? TABLE_NAME_TABLE
					: CommonUtils.getFileNameNoExtension(preset) + "." + DataStructureTemplateWizard.EXT_TABLE;
		} else if (templateExt.equals(DataStructureTemplateWizard.EXT_VIEW)) {
			defaultName = (preset == null) ? VIEW_NAME_VIEW : CommonUtils.getFileNameNoExtension(preset) + "." + DataStructureTemplateWizard.EXT_VIEW;
		} else if (templateExt.equals(DataStructureTemplateWizard.EXT_DSV) || templateExt.equals(DataStructureTemplateWizard.EXT_APPEND)
				|| templateExt.equals(DataStructureTemplateWizard.EXT_DELETE) || templateExt.equals(DataStructureTemplateWizard.EXT_REPLACE)
				|| templateExt.equals(DataStructureTemplateWizard.EXT_UPDATE)) {
			String tableName = ((DataStructureTemplateModel) model).getTableName();
			defaultName = tableName + "." + templateExt;
		}
		return defaultName;
	}

	@Override
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
	}

}
