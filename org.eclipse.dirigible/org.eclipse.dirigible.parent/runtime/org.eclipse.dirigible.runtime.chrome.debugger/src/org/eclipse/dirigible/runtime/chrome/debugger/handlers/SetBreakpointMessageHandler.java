package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.BreakpointRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.BreakpointResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.BreakpointRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.DebuggingService;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;

import com.google.gson.Gson;

public class SetBreakpointMessageHandler implements MessageHandler {

	private static final Gson GSON = new Gson();
	private static final BreakpointRepository REPOSITORY = BreakpointRepository.getInstance();

	@Override
	public void handle(final String message, final Session session) {
		MessageResponse response;
		if (this.isSetByUrl(message)) {
			response = this.handleByUrl(message, session);
		} else if (this.isSetActive(message)) {
			response = this.handleActivateAll(message, session);
		} else {
			response = this.defaultHandle(message, session);
		}

		MessageDispatcher.sendSyncMessage(GSON.toJson(response), session);
	}

	private MessageResponse handleByUrl(final String message, final Session session) {
		final BreakpointRequest request = GSON.fromJson(message, BreakpointRequest.class);
		final Integer messageId = request.getId();
		final Integer lineNumber = request.getLineNumber().intValue();
		final Integer columnNumber = request.getColumnNumber().intValue();
		final String url = request.getUrl();
		Location location = getLocationForRequest(request, lineNumber, columnNumber, url);

		String breakpointId = this.getBreakpointId(lineNumber, columnNumber, url);
		REPOSITORY.add(session.getUserPrincipal().getName(), new Breakpoint(breakpointId, location));
		return this.getResponseMessage(messageId, lineNumber, columnNumber, url);
	}

	private MessageResponse handleActivateAll(final String message, final Session session) {
		final BreakpointRequest request = GSON.fromJson(message, BreakpointRequest.class);
		final Integer messageId = request.getId();
		final Boolean active = request.getActive();
		String userId = session.getUserPrincipal().getName();
		if (active) {
			DebuggingService.activateAllBreakpoints(userId);
		} else {
			DebuggingService.deactivateAllBreakpoints(userId);
		}
		return new EmptyResponse(messageId);
	}

	private MessageResponse defaultHandle(final String message, final Session session) {
		final BreakpointRequest request = GSON.fromJson(message, BreakpointRequest.class);
		final Integer messageId = request.getId();
		final String url = request.getUrl();
		final Integer lineNumber = request.getLineNumber().intValue();
		final Integer columnNumber = request.getColumnNumber().intValue();
		final String condition = request.getCondition(); // TODO:
		Location location = getLocationForRequest(request, lineNumber, columnNumber, url);
		String breakpointId = this.getBreakpointId(lineNumber, columnNumber, url);
		REPOSITORY.add(session.getUserPrincipal().getName(), new Breakpoint(breakpointId, location));
		return this.getResponseMessage(messageId, lineNumber, columnNumber, condition);
	}

	private MessageResponse getResponseMessage(final Integer messageId, final Integer lineNumber,
			final Integer columnNumber, final String url) {
		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("breakpointId", this.getBreakpointId(lineNumber, columnNumber, url));
		final List<Map<String, Object>> locations = new ArrayList<Map<String, Object>>();
		final Map<String, Object> location = this.getLocationMap(lineNumber, columnNumber, url);
		locations.add(location);
		result.put("locations", locations);
		return new BreakpointResponse(messageId, result);
	}

	private Map<String, Object> getLocationMap(final Integer lineNumber, final Integer columnNumber, final String url) {
		final Map<String, Object> location = new HashMap<String, Object>();
		location.put("columnNumber", columnNumber);
		location.put("lineNumber", lineNumber);
		final ScriptRepository repo = ScriptRepository.getInstance();
		location.put("scriptId", repo.getScriptIdByURL(url));
		return location;
	}

	private Location getLocationForRequest(final BreakpointRequest request, final Integer lineNumber,
			final Integer columnNumber, final String url) {
		Location location = request.getLocation();
		if (location == null) {
			location = new Location();
			location.setColumnNumber(Double.valueOf(columnNumber));
			location.setLineNumber(Double.valueOf(lineNumber));
			ScriptRepository scriptRepo = ScriptRepository.getInstance();
			String scriptId = scriptRepo.getScriptIdByURL(url);
			location.setScriptId(scriptId);
		}
		return location;
	}

	private String getBreakpointId(final Integer lineNumber, final Integer columnNumber, final String url) {
		return String.format("%s:%d:%d", url, lineNumber, columnNumber);
	}

	private boolean isSetByUrl(final String message) {
		return message.contains("Debugger.setBreakpointByUrl");
	}

	private boolean isSetActive(final String message) {
		return message.contains("Debugger.setBreakpointsActive");
	}
}
