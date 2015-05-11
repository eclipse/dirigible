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

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeWizardPage;

public class HtmlForEntityTemplateTypePage extends TemplateTypeWizardPage {
	
	private static final String ENTITY_USER_INTERFACE = "EntityUserInterface"; //$NON-NLS-1$

	private static final long serialVersionUID = -9074706494058923056L;

	private static final String LIST_OF_ENTITIES_OPEN_UI5 = Messages.HtmlForEntityTemplateTypePage_LIST_OF_ENTITIES_OPEN_UI5;

	private static final String LIST_AND_DETAILS_VIEW_OPEN_UI5 = Messages.HtmlForEntityTemplateTypePage_LIST_AND_DETAILS_VIEW_OPEN_UI5;

	private static final String LIST_AND_MANAGE_VIEW_OPEN_UI5 = Messages.HtmlForEntityTemplateTypePage_LIST_AND_MANAGE_VIEW_OPEN_UI5;

	private static final String DISPLAY_ENTITY_DETAILS_OPEN_UI5 = Messages.HtmlForEntityTemplateTypePage_DISPLAY_ENTITY_DETAILS_OPEN_UI5;

	private static final String NEW_OR_EDIT_ENTITY_OPEN_UI5 = Messages.HtmlForEntityTemplateTypePage_NEW_OR_EDIT_ENTITY_OPEN_UI5;

	private static final String NEW_OR_EDIT_ENTITY = Messages.HtmlForEntityTemplateTypePage_NEW_OR_EDIT_ENTITY;

	private static final String DISPLAY_ENTITY_DETAILS = Messages.HtmlForEntityTemplateTypePage_DISPLAY_ENTITY_DETAILS;

	private static final String LIST_AND_MANAGE_VIEW = Messages.HtmlForEntityTemplateTypePage_LIST_AND_MANAGE_VIEW;

	private static final String LIST_AND_DETAILS_VIEW = Messages.HtmlForEntityTemplateTypePage_LIST_AND_DETAILS_VIEW;

	private static final String LIST_OF_ENTITIES = Messages.HtmlForEntityTemplateTypePage_LIST_OF_ENTITIES;

	private static final String SELECTION_OF_TEMPLATE_TYPE = Messages.HtmlForEntityTemplateTypePage_SELECTION_OF_TEMPLATE_TYPE;

	private static final String SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION = Messages.HtmlForEntityTemplateTypePage_SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateTypePage"; //$NON-NLS-1$

	private HtmlForEntityTemplateModel model;

	public HtmlForEntityTemplateTypePage(HtmlForEntityTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SELECTION_OF_TEMPLATE_TYPE);
		setDescription(SELECT_THE_TYPE_OF_THE_TEMPLATE_WHICH_WILL_BE_USED_DURING_GENERATION);
	}

//	@Override
//	protected TemplateType[] prepareTemplateTypes() throws MalformedURLException {
//		TemplateType[] templateTypes = new TemplateType[] {
//				TemplateType
//						.createTemplateType(
//								LIST_OF_ENTITIES,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-list.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-list.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								LIST_AND_DETAILS_VIEW,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-details.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-details.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								LIST_AND_MANAGE_VIEW,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-manage.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-manage.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								LIST_OF_ENTITIES_OPEN_UI5,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-list.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-list.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								LIST_AND_DETAILS_VIEW_OPEN_UI5,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-details-open-ui5.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-details.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								LIST_AND_MANAGE_VIEW_OPEN_UI5,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-manage-open-ui5.html", //$NON-NLS-1$
//								"/icons/ui-for-entity-manage.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								DISPLAY_ENTITY_DETAILS_OPEN_UI5,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-display-open-ui5.html", //$NON-NLS-1$
//								"/icons/ui-for-display-entity.png", //$NON-NLS-1$
//								this.getClass()),
//				TemplateType
//						.createTemplateType(
//								NEW_OR_EDIT_ENTITY_OPEN_UI5,
//								"/org/eclipse/dirigible/ide/template/ui/html/templates/ui-for-entity-new-or-edit-open-ui5.html", //$NON-NLS-1$
//								"/icons/ui-for-new-entity.png", //$NON-NLS-1$
//								this.getClass()) };
//		return templateTypes;
//	}
	
	@Override
	protected String getCategory() {
		return ENTITY_USER_INTERFACE;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

}
