/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.v3.io;

import java.util.ArrayList;
import java.util.List;

public class FolderObject extends FileObject {
	
	public FolderObject(String name, String path, String type) {
		super(name, path, type);
	}

	private List<FileObject> files = new ArrayList<>();
	
	private List<FolderObject> folders = new ArrayList<>();
	
	public List<FileObject> getFiles() {
		return files;
	}
	
	public List<FolderObject> getFolders() {
		return folders;
	}

}
