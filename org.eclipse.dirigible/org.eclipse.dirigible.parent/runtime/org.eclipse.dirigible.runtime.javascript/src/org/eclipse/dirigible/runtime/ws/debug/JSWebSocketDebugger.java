package org.eclipse.dirigible.runtime.ws.debug;

import org.eclipse.dirigible.runtime.js.debug.JavaScriptDebugFrame;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

public class JSWebSocketDebugger implements Debugger{
	
	private JavaScriptDebugFrame debugFrame = null;

	public DebugFrame getFrame(Context context, DebuggableScript fnOrScript) {
		context.setDebugger(this, fnOrScript);
		return debugFrame;
	}

	@Override
	public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
	}
}
