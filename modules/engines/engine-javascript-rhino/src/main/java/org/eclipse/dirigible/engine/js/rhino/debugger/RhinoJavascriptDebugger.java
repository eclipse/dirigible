/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.rhino.debugger;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.engine.js.debug.model.DebugModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

public class RhinoJavascriptDebugger implements Debugger {

	private RhinoJavascriptDebugFrame debugFrame = null;

	public RhinoJavascriptDebugger(DebugModel debugModel, HttpServletRequest request) {
		this.debugFrame = new RhinoJavascriptDebugFrame(debugModel, request, this);
	}

	public DebugFrame getFrame(Context context, DebuggableScript fnOrScript) {
		context.setDebugger(this, fnOrScript);
		return debugFrame;
	}

	@Override
	public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
	}

}
