package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.DebuggingService;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;

import com.google.gson.Gson;

/**
 * <pre>
 * Handler for message with method Debugger.stepInto, Debugger.stepOver or Debugger.stepOut
 * The flow is:
 * 		1. Receive message
 * 		2. Send empty result
 * 		3. Send notification Debugger.resumed with empty parameters
 * 		4. Send notification Debugger.paused
 * </pre>
 */
public class DebuggerStepHandler implements MessageHandler {

	private static final Gson GSON = new Gson();

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final MessageResponse response = new EmptyResponse(RequestUtils.getMessageId(message));
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
		if (RequestUtils.isStepIntoMessage(message)) {
			this.handleStepInto(message, session);
		} else if (RequestUtils.isStepOutMessage(message)) {
			this.handleStepOut(message, session);
		} else if (RequestUtils.isStepOverMessage(message)) {
			this.handleStepOver(message, session);
		}
	}

	private void handleStepOver(final String message, final Session session) {
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepOver(session.getUserPrincipal().getName(), getBreakpointLine(session));
	}

	private void handleStepOut(final String message, final Session session) {
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepOut(session.getUserPrincipal().getName(), getBreakpointLine(session));
	}

	private void handleStepInto(final String message, final Session session) {
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepInto(session.getUserPrincipal().getName(), getBreakpointLine(session));
	}

	private int getBreakpointLine(Session session){
		Location currentExecutionLocation = DebugConfiguration.getCurrentExecutionLocation(session.getUserPrincipal().getName());
		return currentExecutionLocation.getLineNumber().intValue();
	}

}
