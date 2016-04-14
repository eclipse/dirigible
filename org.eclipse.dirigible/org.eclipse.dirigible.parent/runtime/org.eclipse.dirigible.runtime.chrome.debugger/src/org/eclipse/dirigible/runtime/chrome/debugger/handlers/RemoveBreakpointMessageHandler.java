package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.BreakpointRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.BreakpointRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;

import com.google.gson.Gson;

public class RemoveBreakpointMessageHandler implements MessageHandler{

	private static final Gson GSON = new Gson();

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final BreakpointRequest request = GSON.fromJson(message, BreakpointRequest.class);
		final Integer messageId = request.getId();
		//TODO:
		final String breakpointId = (String) request.getParams().get("breakpointId");
		final BreakpointRepository repo = BreakpointRepository.getInstance();
		repo.removeById(session.getUserPrincipal().getName(), breakpointId);
		MessageDispatcher.sendMessage(GSON.toJson(new EmptyResponse(messageId)), session);
	}
}
