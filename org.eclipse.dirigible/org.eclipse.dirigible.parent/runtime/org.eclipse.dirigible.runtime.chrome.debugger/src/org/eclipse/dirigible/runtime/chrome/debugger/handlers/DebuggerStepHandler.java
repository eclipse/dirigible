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
import org.eclipse.dirigible.runtime.chrome.debugger.utils.ScriptUtils;

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
		String userId = session.getUserPrincipal().getName();
		setConfigurationForStepOver(userId);
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepOver(userId, getBreakpointLine(session));
	}

	private void setConfigurationForStepOver(String userId) {
		Location currentLocation = DebugConfiguration.getCurrentExecutionLocation(userId);
		if(isFunctionExecutionStatement(currentLocation)){
			//TODO: execute
		}
		Location location = getStepIntoLocation(userId);
		DebugConfiguration.setCurrentExecutionLocation(userId, location);
	}

	private boolean isFunctionExecutionStatement(Location currentLocation) {
		//TODO: 
		return false;
	}

	private void handleStepOut(final String message, final Session session) {
		String userId = session.getUserPrincipal().getName();
		setConfigurationForStepOut(userId);
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepOut(userId, getBreakpointLine(session));
	}

	private void setConfigurationForStepOut(final String userId) {
		Location nextLocation = getStepOutLocation(userId);
		DebugConfiguration.setCurrentExecutionLocation(userId, nextLocation);
	}

	private Location getStepOutLocation(String userId) {
		Location location = DebugConfiguration.getCurrentExecutionLocation(userId);
		// in case the current execution location is placed inside a function scope
		// then the next execution location is the first non-empty line after the function
		if(isInFunction(location)){ 
			return nextLocationAfterFunction(userId);
		}
		return getStepIntoLocation(userId);
	}

	private boolean isInFunction(Location location) {
		Double line = location.getLineNumber();
		String scriptId = location.getScriptId();
		String functionForLine = ScriptUtils.getEnclosingFunctionName(scriptId, line.intValue());
		return functionForLine != null;
	}

	private Location nextLocationAfterFunction(String userId) {
		Location location = DebugConfiguration.getCurrentExecutionLocation(userId);
		Double lineNumber = location.getLineNumber();
		String scriptId = location.getScriptId();
		Location functionEndLocation = ScriptUtils.getEndLocation(scriptId, lineNumber.intValue());
		return ScriptUtils.getFirstLocationAfter(functionEndLocation);
	}

	private void handleStepInto(final String message, final Session session) {
		String userId = session.getUserPrincipal().getName();
		setConfigurationForStepInto(userId);
		DebuggerPausedRequest.sendRequest(session);
		DebuggingService.stepInto(userId, getBreakpointLine(session));
	}

	private void setConfigurationForStepInto(final String userId) {
		//TODO: if line is function call then go to function definition
		Location nextLine = getStepIntoLocation(userId);
		DebugConfiguration.setCurrentExecutionLocation(userId, nextLine);
	}

	private Location getStepIntoLocation(String userId) {
		Location location = DebugConfiguration.getCurrentExecutionLocation(userId);
		String currentScriptId = location.getScriptId();
		Double currentLineNumber = location.getLineNumber();
		Double newLineNumber = currentLineNumber + 1.0;
		Location nextLine = new Location();
		nextLine.setColumnNumber(ScriptUtils.getStartColumnForLine(currentScriptId, newLineNumber));
		nextLine.setLineNumber(newLineNumber);
		nextLine.setScriptId(currentScriptId);
		return nextLine;
	}

	private int getBreakpointLine(Session session){
		Location currentExecutionLocation = DebugConfiguration.getCurrentExecutionLocation(session.getUserPrincipal().getName());
		return currentExecutionLocation.getLineNumber().intValue();
	}

}
