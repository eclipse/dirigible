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

package org.eclipse.dirigible.repository.ext.debug;


public interface IDebugController extends IDebugView {
	
	public void refresh();

	public void stepInto();

	public void stepOver();

	public void continueExecution();

	public void skipAllBreakpoints();

	public void setBreakpoint(String path, int row);

	public void clearBreakpoint(String path, int row);

	public void clearAllBreakpoints();

//	public void clearAllBreakpoints(String path);

}
