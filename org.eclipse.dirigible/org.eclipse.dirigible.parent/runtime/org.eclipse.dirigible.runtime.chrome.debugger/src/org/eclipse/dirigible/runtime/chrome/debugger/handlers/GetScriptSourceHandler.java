package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.ErrorResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.ResultResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.BreakpointRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;

import com.google.gson.Gson;

/**
 * <pre>
 * Handler for message with method Debugger.getScriptSource
 *
 * This handler is executed either when the DevTools UI is initialized for the first time, a script resource is
 * requested or when the page is reloaded.
 *
 * The flow is:
 * 1. Receive message
 * 2. Load script source as follows:
 * 2.1. Scan the script for set breakpoints. If there are breakpoints, pause execution on first breakpoint:
 * 2.1.1. Send notification with method Debugger.breakpointResolved
 * 2.1.2. Send notification with method Debugger.paused
 * </pre>
 *
 */
public class GetScriptSourceHandler implements MessageHandler {

	private static final Gson GSON = new Gson();

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		final Integer id = request.getId();
		final Map<String, Object> params = request.getParams();
		final String scriptId = (String) params.get("scriptId");
		final ScriptRepository repository = ScriptRepository.getInstance();
		final String scriptSource = repository.getSourceFor(scriptId);
		MessageResponse response;
		if (scriptSource == null) {
			response = new ErrorResponse(id, String.format("No script for id: %s", scriptId));
		} else {
			if (navigatedDebuggableScript()) {
				this.sendDebugNotifications(scriptId, session);
			}
			final Map<String, Object> result = new HashMap<String, Object>();
			result.put("scriptSource", scriptSource);
			response = new ResultResponse(id, result);
		}
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
	}

	private boolean navigatedDebuggableScript() {
		return MessageDispatcher.sentMessageContain("Page.frameStartedLoading");
	}

	private void sendDebugNotifications(final String scriptId, final Session session) {
		final BreakpointRepository breakpointRepo = BreakpointRepository.getInstance();
		String userId = session.getUserPrincipal().getName();
		final Set<Breakpoint> sortedBreakpoints = breakpointRepo.getSortedBreakpointsForScript(userId, scriptId);
		Breakpoint firstBreakpoint = sortedBreakpoints.toArray(new Breakpoint[sortedBreakpoints.size()])[0];
		final Integer breakpointLine = firstBreakpoint.getLocation().getLineNumber().intValue();;
		for (final Breakpoint b : sortedBreakpoints) {
			this.sendBreakpointResolved(b, session);
		}
		DebuggerPausedRequest.sendRequest(scriptId, session, breakpointLine);
	}

	private void sendBreakpointResolved(final Breakpoint breakpoint, final Session session) {
		final MessageRequest request = new MessageRequest();
		request.setId(null);
		request.setMethod("Debugger.breakpointResolved");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("breakpointId", breakpoint.getId());
		String userId = session.getUserPrincipal().getName();
		DebugConfiguration.setCurrentExecutionLocation(userId, breakpoint.getLocation());
		params.put("location", DebugConfiguration.getCurrentExecutionLocation(userId));
		MessageDispatcher.sendMessage(GSON.toJson(request), session);
	}
}
