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

package org.eclipse.dirigible.ide.template.ui.common;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;

public class TemplateTypeDescriptor {
	
	private String text;

	private String location;

	private String image;
	
	private String category;

	private Set<String> parameters = new HashSet<String>();
	
	private Set<WizardPage> pages = new HashSet<WizardPage>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<String> getParameters() {
		return parameters;
	}

	public void setParameters(Set<String> parameters) {
		this.parameters = parameters;
	}

	public Set<WizardPage> getPages() {
		return pages;
	}

	public void setPages(Set<WizardPage> pages) {
		this.pages = pages;
	}
	

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

}
