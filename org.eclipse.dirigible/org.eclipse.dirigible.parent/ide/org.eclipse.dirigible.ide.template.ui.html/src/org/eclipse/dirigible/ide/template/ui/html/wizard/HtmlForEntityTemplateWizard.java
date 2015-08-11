/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;


public class HtmlForEntityTemplateWizard extends TemplateWizard {

	private static final String CREATE_USER_INTERFACE_FOR_RES_TFUL_PERSISTENCE_SERVICE = Messages.HtmlForEntityTemplateWizard_CREATE_USER_INTERFACE_FOR_RES_TFUL_PERSISTENCE_SERVICE;
	private final HtmlForEntityTemplateModel model;
	private final HtmlForEntityTemplateTablePage tablePage;
	private final HtmlForEntityTemplateTypePage typesPage;
	private final HtmlForEntityTemplateTargetLocationPage targetLocationPage;
	private final HtmlForEntityTemplateTitlePage titlePage;

	public HtmlForEntityTemplateWizard(IFile file) {
		setWindowTitle(CREATE_USER_INTERFACE_FOR_RES_TFUL_PERSISTENCE_SERVICE);

		model = new HtmlForEntityTemplateModel();
		model.setSourceResource(file);
		typesPage = new HtmlForEntityTemplateTypePage(model);
		tablePage = new HtmlForEntityTemplateTablePage(model);
		targetLocationPage = new HtmlForEntityTemplateTargetLocationPage(model);
		titlePage = new HtmlForEntityTemplateTitlePage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(tablePage);
		addPage(targetLocationPage);
		addPage(titlePage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		HtmlForEntityTemplateGenerator generator = new HtmlForEntityTemplateGenerator(
				model);
		return generator;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof HtmlForEntityTemplateTypePage) {
			if (("/org/eclipse/dirigible/ide/template/ui/html/templates/angular/display_single_entity/ui-for-display-single-entity.html" //$NON-NLS-1$
					.equals(model.getTemplate().getLocation()))) {
				return targetLocationPage;
			}
			if (("/org/eclipse/dirigible/ide/template/ui/html/templates/angular/new_or_edit/ui-for-new-or-edit-entity.html" //$NON-NLS-1$
					.equals(model.getTemplate().getLocation()))) {
				return targetLocationPage;
			}
		}
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					model.getFileName()));
		}
		return result;
	}

}
