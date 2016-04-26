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
	
	public static synchronized void sendRequest(Session session){
		Location location = DebugConfiguration.getCurrentExecutionLocation(session.getUserPrincipal().getName());
		String scriptId = location.getScriptId();
		int breakpointLine = location.getLineNumber().intValue();
		sendRequest(scriptId, session, breakpointLine);
	}
	
	public static void sendRequest(final String scriptId, final Session session, final Integer breakpointLine) {
		final MessageRequest debuggerPausedRequest = new DebuggerPausedRequest();
		final Map<String, Object> params = new HashMap<String, Object>();
		
		String userId = session.getUserPrincipal().getName();
		final List<CallFrame> callFrames = CallFrameProvider.get(userId, scriptId, breakpointLine);
		params.put("callFrames", callFrames);
		persistFrames(callFrames);

		final BreakpointRepository breakpointRepo = BreakpointRepository.getInstance();
		Set<Breakpoint> breakpoints = breakpointRepo.getSortedBreakpointsForScript(userId, scriptId);
		for(Breakpoint b : breakpoints){
			int bpLine = b.getLocation().getLineNumber().intValue();
			if(bpLine == breakpointLine){
				final List<String> hitBreakpoints = new ArrayList<String>();
				hitBreakpoints.add(b.getId());
				params.put("hitBreakpoints", hitBreakpoints);
				break;
			}
		}
		final String reason = "other";
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
