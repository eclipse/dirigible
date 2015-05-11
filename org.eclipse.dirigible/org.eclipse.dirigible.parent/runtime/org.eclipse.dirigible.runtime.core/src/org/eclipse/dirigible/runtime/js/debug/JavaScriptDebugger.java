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

package org.eclipse.dirigible.runtime.js.debug;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

import org.eclipse.dirigible.repository.ext.debug.IDebugProtocol;

public class JavaScriptDebugger implements Debugger {

	private JavaScriptDebugFrame debugFrame = null;

	public JavaScriptDebugger(IDebugProtocol debugProtocol, HttpServletRequest request) {
		this.debugFrame = new JavaScriptDebugFrame(debugProtocol, request, this);
	}

	public DebugFrame getFrame(Context context, DebuggableScript fnOrScript) {
		context.setDebugger(this, fnOrScript);
		return debugFrame;
	}

	@Override
	public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
	}

}
