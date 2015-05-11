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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.dirigible.ide.shared.editor.SourceFileEditorInput;
import org.eclipse.dirigible.repository.api.ICommonConstants.ARTIFACT_TYPE;

public class EditorInputFactory {
	
	public static FileEditorInput createInput(IFile file, int row, String contentType) {
		SourceFileEditorInput input = new SourceFileEditorInput(file);
		input.setRow(row);
		breakpointsSupported(file, contentType, input);
		readonlyEnabled(file, contentType, input);
		return input;
	}
	
	static void readonlyEnabled(IFile file, String contentType,
			SourceFileEditorInput input) {
//		input.setReadOnly(true);
	}

	static void breakpointsSupported(IFile file, String contentType,
			SourceFileEditorInput input) {
		if (file.getRawLocation().toString().contains(ARTIFACT_TYPE.SCRIPTING_SERVICES)
				&& contentType != null
				&& contentType.contains("javascript")) {
			input.setBreakpointsEnabled(true);
		}
	}


}
