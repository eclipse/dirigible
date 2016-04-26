package org.eclipse.dirigible.runtime.ws.debug;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugExecutor.DebugCommand;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.BreakpointRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.js.debug.JavaScriptDebugFrame;
import org.eclipse.dirigible.runtime.js.debug.WebSocketDebugBridgeServletInternal;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.debug.Debugger;

public class WebSocketDebugFrame extends JavaScriptDebugFrame {

	private static boolean sentOnEnterPause = false;
	
	public WebSocketDebugFrame(DebugModel debugModel, HttpServletRequest request, Debugger javaScriptDebugger) {
		super(debugModel, request, javaScriptDebugger);
	}
	
	@Override
	public void onEnter(Context context, Scriptable activation, Scriptable thisObj, Object[] args) {
		super.onEnter(context, activation, thisObj, args);
		if(!sentOnEnterPause){
			sendDebuggerPause();
			sentOnEnterPause = true;
//			DebugConfiguration.setUserVablesForObjectId(userId, objectId, variablesForObjectId);
		}
	}

	private void sendDebuggerPause() {
		String userId = debuggerActionCommander.getUserId();
		List<Session> userSessions = WebSocketDebugBridgeServletInternal.getSessionsForUser(userId);
		if (userSessions != null && !userSessions.isEmpty()) {
			Session session = userSessions.get(0);
			DebuggerPausedRequest.sendRequest(session);
		}
	}

	@Override
	protected void hitBreakpoint(int lineNumber) {
		super.hitBreakpoint(lineNumber);
		sendDebuggerPause();
	}

	@Override
	protected boolean isBreakpoint(int lineNumber) {
		String path = scriptStack.peek().getSourceName();
		String userId = debuggerActionCommander.getUserId();
		BreakpointRepository breakpointRepo = BreakpointRepository.getInstance();
		ScriptRepository scriptRepo = ScriptRepository.getInstance();
		Set<Breakpoint> userBreakpoints = breakpointRepo.getUserBreakpoints(userId);
		for (Breakpoint b : userBreakpoints) {
			String scriptId = b.getLocation().getScriptId();
			String scriptUrlForBreakpoint = scriptRepo.getUrl(scriptId);
			if (scriptUrlForBreakpoint.equalsIgnoreCase(path)) {
				int breakpointLineNumber = b.getLocation().getLineNumber().intValue();
				if (lineNumber == breakpointLineNumber) {
					return true;
				}
			}
		}
		return false;
	}

	protected DebugCommand getNextCommand() {
		return debuggerActionCommander.getCommand();
	}

	@Override
	public void onExit(Context cx, boolean byThrow, Object resultOrException) {

	}

}
