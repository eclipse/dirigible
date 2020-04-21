/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.git.model;

import java.util.List;

import org.eclipse.dirigible.core.git.GitChangedFile;

public class GitProjectChangedFiles {
	
	private List<GitChangedFile> files;
	
	/**
	 * Getter for the files
	 * 
	 * @return the files
	 */
	public List<GitChangedFile> getFiles() {
		return files;
	}

	/**
	 * Setter for the files
	 * 
	 * @param files the files to set
	 */
	public void setFiles(List<GitChangedFile> files) {
		this.files = files;
	}

}
