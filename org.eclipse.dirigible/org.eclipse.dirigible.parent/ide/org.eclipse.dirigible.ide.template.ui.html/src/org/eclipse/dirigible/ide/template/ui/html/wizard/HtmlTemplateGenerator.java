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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;

public class HtmlTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "HTML_GENERATOR"; //$NON-NLS-1$

	private HtmlTemplateModel model;

	public HtmlTemplateGenerator(HtmlTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pageTitle", model.getPageTitle()); //$NON-NLS-1$
		return parameters;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

	@Override
	public void generate() throws Exception {
		super.generate();
		if ("/org/eclipse/dirigible/ide/template/ui/html/templates/index-page.html" //$NON-NLS-1$
				.equals(model.getTemplate().getLocation())) {
			copyFile(
					"main.menu", //$NON-NLS-1$
					"/org/eclipse/dirigible/ide/template/ui/html/templates/main.menu", //$NON-NLS-1$
					HtmlTemplateGenerator.class);
			copyFile(
					"sample.html", //$NON-NLS-1$
					"/org/eclipse/dirigible/ide/template/ui/html/templates/sample.html", //$NON-NLS-1$
					HtmlTemplateGenerator.class);
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/html/templates/header.html", //$NON-NLS-1$
					model.getTargetLocation(), "header.html"); //$NON-NLS-1$
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/html/templates/footer.html", //$NON-NLS-1$
					model.getTargetLocation(), "footer.html"); //$NON-NLS-1$
		}
		if ("/org/eclipse/dirigible/ide/template/ui/html/templates/index-page-openui5.html" //$NON-NLS-1$
				.equals(model.getTemplate().getLocation())) {
			copyFile(
					"main.menu", //$NON-NLS-1$
					"/org/eclipse/dirigible/ide/template/ui/html/templates/main-openui5.menu", //$NON-NLS-1$
					HtmlTemplateGenerator.class);
			copyFile(
					"sample.html", //$NON-NLS-1$
					"/org/eclipse/dirigible/ide/template/ui/html/templates/sample-openui5.html", //$NON-NLS-1$
					HtmlTemplateGenerator.class);
		}
	}

	@Override
	protected byte[] afterGeneration(byte[] bytes) {
		String content = new String(bytes);
		content = content.replace("\\$", "$"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\{", "{"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\}", "}"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\[", "["); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\]", "]"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
		byte[] result = content.getBytes();
		return result;
	}

}
