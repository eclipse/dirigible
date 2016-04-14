package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.BreakpointRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.ScriptUtils;

import com.google.gson.Gson;

public class DebuggerPausedRequest extends MessageRequest {

	public DebuggerPausedRequest() {
		this.id = null;
		this.method = "Debugger.paused";
	}

	public static class CallFrame {
		private String callFrameId;
		private Location functionLocation;
		private String functionName;
		private Location location;
		private List<Scope> scopeChain;

		public String getCallFrameId() {
			return this.callFrameId;
		}

		public void setCallFrameId(final String callFrameId) {
			this.callFrameId = callFrameId;
		}

		public Location getFunctionLocation() {
			return this.functionLocation;
		}

		public void setFunctionLocation(final Location functionLocation) {
			this.functionLocation = functionLocation;
		}

		public String getFunctionName() {
			return this.functionName;
		}

		public void setFunctionName(final String functionName) {
			this.functionName = functionName;
		}

		public Location getLocation() {
			return this.location;
		}

		public void setLocation(final Location location) {
			this.location = location;
		}

		public List<Scope> getScopeChain() {
			return this.scopeChain;
		}

		public void setScopeChain(final List<Scope> scopeChain) {
			this.scopeChain = scopeChain;
		}
	}

	public static class Scope {
		private String type;
		private Map<String, String> object;
		private Location startLocation;
		private Location endLocation;

		public String getType() {
			return this.type;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public Map<String, String> getObject() {
			return this.object;
		}

		public void setObject(final Map<String, String> object) {
			this.object = object;
		}

		public Location getStartLocation() {
			return this.startLocation;
		}

		public void setStartLocation(final Location startLocation) {
			this.startLocation = startLocation;
		}

		public Location getEndLocation() {
			return this.endLocation;
		}

		public void setEndLocation(final Location endLocation) {
			this.endLocation = endLocation;
		}
	}
	

	private static Integer currentObjectId = 1;
	
	public static synchronized void sendRequest(Session session){
		Location location = DebugConfiguration.getCurrentExecutionLocation(session.getUserPrincipal().getName());
		String scriptId = location.getScriptId();
		int breakpointLine = location.getLineNumber().intValue();
		sendRequest(scriptId, session, breakpointLine);
	}
	
	public static void sendRequest(final String scriptId, final Session session, final Integer breakpointLine) {
		final MessageRequest debuggerPausedRequest = new DebuggerPausedRequest();
		final Map<String, Object> params = new HashMap<String, Object>();
		final Location startLocation = ScriptUtils.getStartLocation(scriptId, breakpointLine);

		// TODO: create call frames
		final List<CallFrame> callFrames = new ArrayList<CallFrame>();
		final CallFrame callFrame = new CallFrame();
		callFrame.setFunctionLocation(startLocation);
		String userId = session.getUserPrincipal().getName();
		callFrame.setLocation(DebugConfiguration.getCurrentExecutionLocation(userId));

		final int injectedScriptId = 0; // common for all callFrames within a
										// script
		int ordinal = 0; // increment with each callFrame

		final List<Scope> scopeChain = new ArrayList<Scope>();
		final Scope scope = new Scope();
		scope.setEndLocation(ScriptUtils.getEndLocation(scriptId, breakpointLine));
		scope.setStartLocation(startLocation);
		scope.setEndLocation(ScriptUtils.getEndLocation(scriptId, breakpointLine));
		
		final Map<String, String> object = new HashMap<String, String>();
		object.put("className", "Object");
		object.put("description", "Object");
		object.put("objectId",
				String.format("{\"injectedScriptId\":%d,\"id\":%d", injectedScriptId, currentObjectId++));
		object.put("type", "object");
		scope.setObject(object);
		scope.setType("local");
		scopeChain.add(scope);
		
		callFrame.setCallFrameId(String.format("{\"ordinal\":%d,\"injectedScriptId\":%d}", ordinal, injectedScriptId)); 
		callFrame.setScopeChain(scopeChain);
		callFrame.setFunctionName(ScriptUtils.getEnclosingFunctionName(scriptId, breakpointLine));
		callFrames.add(callFrame);
		params.put("callFrames", callFrames);
		persistFrames(callFrames);

		final BreakpointRepository breakpointRepo = BreakpointRepository.getInstance();
		Set<Breakpoint> breakpoints = breakpointRepo.getSortedBreakpointsForScript(userId, scriptId);
		final Breakpoint firstBreakpoint = breakpoints.toArray(new Breakpoint[breakpoints.size()])[0];
		final List<String> hitBreakpoints = new ArrayList<String>();
		hitBreakpoints.add(firstBreakpoint.getId());
		final String reason = "other";
		params.put("hitBreakpoints", hitBreakpoints);
		params.put("reason", reason);
		debuggerPausedRequest.setParams(params);
		MessageDispatcher.sendMessage(new Gson().toJson(debuggerPausedRequest), session);
	}
	
	private static void persistFrames(final List<CallFrame> callFrames) {
		final ScriptRepository scriptRepo = ScriptRepository.getInstance();
		for (final CallFrame frame : callFrames) {
			scriptRepo.addFrame(frame);
		}
	}
}
