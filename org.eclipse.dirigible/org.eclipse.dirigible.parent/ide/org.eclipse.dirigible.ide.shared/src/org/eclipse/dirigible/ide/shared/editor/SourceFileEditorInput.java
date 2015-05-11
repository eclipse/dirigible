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

package org.eclipse.dirigible.ide.shared.editor;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

public class SourceFileEditorInput extends FileEditorInput {
	
	private boolean readOnly = false;
	
	private boolean breakpointsEnabled = false;
	
	private int row = 0;

	public SourceFileEditorInput(IFile file) {
		super(file);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isBreakpointsEnabled() {
		return breakpointsEnabled;
	}

	public void setBreakpointsEnabled(boolean breakpointsEnabled) {
		this.breakpointsEnabled = breakpointsEnabled;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public IFile getFile() {
		// TODO Auto-generated method stub
		return super.getFile();
	}
	
	@Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return super.getURI();
	}
	
	
}
