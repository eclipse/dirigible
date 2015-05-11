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

package org.eclipse.dirigible.ide.debug.model;

import java.beans.PropertyChangeListener;

import org.eclipse.ui.IEditorPart;

public interface IDebugController extends PropertyChangeListener {
	
	public void refresh(DebugModel debugModel);

	public void stepInto(DebugModel debugModel);

	public void stepOver(DebugModel debugModel);

	public void continueExecution(DebugModel debugModel);

	public void skipAllBreakpoints(DebugModel debugModel);

	public IEditorPart openEditor(String path, int row);

	public void setBreakpoint(DebugModel debugModel, String path, int row);

	public void clearBreakpoint(DebugModel debugModel, String path, int row);

	public void clearAllBreakpoints(DebugModel debugModel);

	public void clearAllBreakpoints(DebugModel debugModel, String path);

}
