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

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import org.eclipse.dirigible.ide.workspace.wizard.project.create.ProjectTemplateType;

public abstract class SamplesModel {
	private SamplesCategory parent;
	private ProjectTemplateType template;

	protected void setParent(SamplesCategory parent) {
		this.parent = parent;
	}

	public SamplesCategory getParent() {
		return parent;
	}

	public SamplesModel() {

	}

	public SamplesModel(ProjectTemplateType template) {
		this.template = template;
	}

	public ProjectTemplateType getTemplate() {
		return template;
	}

	public void setTemplate(ProjectTemplateType template) {
		this.template = template;
	}
}
