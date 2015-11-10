/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import java.io.IOException;

import org.eclipse.dirigible.repository.ext.fs.FileZipImporter;

public class LocalZipImporter extends FileZipImporter {

	@Override
	protected String getMappedLocation(String destinationFolder) throws IOException {
		String workspaceFolder = LocalWorkspaceMapper.getMappedName(destinationFolder);
		return workspaceFolder;
	}

}
