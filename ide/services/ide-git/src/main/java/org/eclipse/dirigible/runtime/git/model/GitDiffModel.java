/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.git.model;

public class GitDiffModel {

	private String original;
	private String modified;

	public GitDiffModel() {
		
	}

	public GitDiffModel(String original, String modified) {
		this.original = original;
		this.modified = modified;
	}
	
	/**
	 * @return the original text
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * @param original the original text to set
	 */
	public void setOriginal(String original) {
		this.original = original;
	}

	/**
	 * @return the modified text
	 */
	public String getModified() {
		return modified;
	}

	/**
	 * @param modified the modified text to set
	 */
	public void setModified(String modified) {
		this.modified = modified;
	}

}
