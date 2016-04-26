package org.eclipse.dirigible.runtime.ws.debug;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

public class JSWebSocketDebugger implements Debugger{
	
	private DebugFrame debugFrame = null;

	public JSWebSocketDebugger(DebugModel debugModel, HttpServletRequest request) {
		this.debugFrame = new WebSocketDebugFrame(debugModel, request, this);
	}

	public DebugFrame getFrame(Context context, DebuggableScript fnOrScript) {
		context.setDebugger(this, fnOrScript);
		return debugFrame;
	}

	@Override
	public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
	}
}
