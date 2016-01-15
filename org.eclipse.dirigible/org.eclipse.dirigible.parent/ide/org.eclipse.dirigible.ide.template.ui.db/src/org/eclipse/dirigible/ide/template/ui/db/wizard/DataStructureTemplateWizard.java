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

import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;
import org.eclipse.jface.wizard.IWizardPage;

public class DataStructureTemplateWizard extends TemplateWizard {

	public static final String EXT_DSV = "dsv";
	public static final String EXT_VIEW = "view";
	public static final String EXT_TABLE = "table";

	private final DataStructureTemplateModel model;
	private final DataStructureTemplateTypePage typesPage;
	private final DataStructureTemplateStructurePage structurePage;
	private final DataStructureTemplateQueryPage queryPage;
	private final DataStructureTemplateDSVPage dsvPage;
	private final DataStructureTemplateTargetLocationPage targetLocationPage;

	public DataStructureTemplateWizard(IResource resource) {
		setWindowTitle(Messages.DataStructureTemplateWizard_CREATE_DATA_STRUCTURE);

		model = new DataStructureTemplateModel();
		model.setSourceResource(resource);
		typesPage = new DataStructureTemplateTypePage(model);
		structurePage = new DataStructureTemplateStructurePage(model);
		queryPage = new DataStructureTemplateQueryPage(model);
		dsvPage = new DataStructureTemplateDSVPage(model);
		targetLocationPage = new DataStructureTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(structurePage);
		addPage(queryPage);
		addPage(dsvPage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		DataStructureTemplateGenerator generator = new DataStructureTemplateGenerator(model);
		return generator;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = null;
		if (page instanceof DataStructureTemplateTypePage) {
			if (EXT_TABLE.equals(model.getTemplate().getExtension())) {
				nextPage = structurePage;
			} else if (EXT_VIEW.equals(model.getTemplate().getExtension())) {
				nextPage = queryPage;
			} else if (EXT_DSV.equals(model.getTemplate().getExtension())) {
				nextPage = dsvPage;
			}
		} else if ((page instanceof DataStructureTemplateStructurePage) || (page instanceof DataStructureTemplateQueryPage)
				|| (page instanceof DataStructureTemplateDSVPage)) {

			if (page instanceof DataStructureTemplateQueryPage) {
				DataStructureTemplateQueryPage queryPage = (DataStructureTemplateQueryPage) page;
				String queryText = queryPage.getQuery();
				if ((queryText == null) || "".equals(queryText.trim())) {
					nextPage = page;
					queryPage.setErrorMessage("SQL query string is empty");
				} else {
					queryPage.setErrorMessage(null);
					this.model.setQuery(queryText);
				}
			}

			nextPage = targetLocationPage;
		} else {
			nextPage = super.getNextPage(page);
		}

		return nextPage;
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED, model.getFileName()));
		}
		return result;
	}

	@Override
	public boolean canFinish() {
		boolean can = super.canFinish();
		if (!can) {
			return can;
		}
		if (EXT_VIEW.equals(model.getTemplate().getExtension())) {
			String queryText = queryPage.getQuery();
			if ((queryText == null) || "".equals(queryText.trim())) {
				queryPage.setErrorMessage("SQL query string is empty");
				can = false;
			} else {
				queryPage.setErrorMessage(null);
				this.model.setQuery(queryText);
				can = true;
			}
		}
		return can;
	}

}
