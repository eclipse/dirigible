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

import java.util.ArrayList;
import java.util.List;

public class SamplesCategory extends SamplesModel {
	private String name;
	private List<SamplesCategory> categories;
	private List<SamplesProject> samples;

	public SamplesCategory(String name) {
		this(name, new ArrayList<SamplesProject>());
	}

	public SamplesCategory(String name, List<SamplesProject> samples) {
		this(name, new ArrayList<SamplesCategory>(), samples);
	}

	public SamplesCategory(String name, List<SamplesCategory> categories,
			List<SamplesProject> samples) {
		this.name = name;
		this.categories = categories;
		this.samples = samples;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SamplesProject> getSamples() {
		return samples;
	}

	public void addSample(SamplesProject sample) {
		sample.setParent(this);
		this.samples.add(sample);
	}

	public List<SamplesCategory> getCategories() {
		return categories;
	}

	public void addCategory(SamplesCategory category) {
		category.setParent(this);
		this.categories.add(category);
	}
}
