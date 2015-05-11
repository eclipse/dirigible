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

package org.eclipse.dirigible.ide.workspace.dual;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.part.FileEditorInput;

public class EditorInputFactory {
	
	public static FileEditorInput createInput(IFile file, int row, String contentType) {
		Workspace workspace=(Workspace)ResourcesPlugin.getWorkspace();
		IFile fileRcp = workspace.getRoot().getFile(file.getFullPath());
		FileEditorInput input = new FileEditorInput(fileRcp);
		return input;
	}

}
