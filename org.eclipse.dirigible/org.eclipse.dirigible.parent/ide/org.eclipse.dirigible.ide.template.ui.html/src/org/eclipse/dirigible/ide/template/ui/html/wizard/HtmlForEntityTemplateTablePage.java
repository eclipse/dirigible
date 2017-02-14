/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.dirigible.ide.template.ui.common.WizardForEntityTamplateTablePage;
import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.jface.viewers.IBaseLabelProvider;

public class HtmlForEntityTemplateTablePage extends WizardForEntityTamplateTablePage {

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateTablePage"; //$NON-NLS-1$

	protected HtmlForEntityTemplateTablePage(ContentForEntityModel model) {
		super(model, PAGE_NAME);
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new HtmlForEntityTemplateTablePageLabelProvider();
	}
}
