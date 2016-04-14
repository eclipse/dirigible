package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;

import com.google.gson.Gson;

public class SetScriptSourceHandler implements MessageHandler{

	private static final Gson GSON = new Gson();
	
	@Override
	public void handle(String message, Session session) throws IOException {
		MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		Integer messageId = request.getId();
		Map<String, Object> params = request.getParams();
		String scriptId = (String) params.get("scriptId");
		String source = (String) params.get("scriptSource");
		
		ScriptRepository repo = ScriptRepository.getInstance();
		repo.update(scriptId, source);
		EmptyResponse response = new EmptyResponse(messageId);
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
		
		//TODO: set script in rhino
	}

}
