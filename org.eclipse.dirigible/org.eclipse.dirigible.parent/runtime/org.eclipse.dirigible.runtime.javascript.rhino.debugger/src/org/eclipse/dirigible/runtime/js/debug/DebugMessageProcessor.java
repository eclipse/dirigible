package org.eclipse.dirigible.runtime.js.debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.ResultResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.handlers.MessageHandler;
import org.eclipse.dirigible.runtime.chrome.debugger.handlers.MessageHandlerFactory;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;

import com.google.gson.Gson;

public class DebugMessageProcessor {
	private static final Gson GSON = new Gson();

	public synchronized void processMessage(String message, Session session) throws IOException {
		if (org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils.isInspectorEnable(message)) {
			sendInitialConfiguration(session);
			return;
		} else if (receivedInspectorEnable(session)) {
			handle(message, session);
		}
	}

	private void sendInitialConfiguration(Session session) throws IOException {
		Integer sweId = sendServiceWorkerEnable(session);
		Integer cenId = sendCanEmulateNetwork(session);
		Integer ieId = sendInspectorEnable(session);

		List<String> receivedMessages = MessageDispatcher.receivedMessages(session.getId());
		if (receivedMessages != null) {
			for (String message : receivedMessages) {
				if (shouldProcessMessage(message, sweId, cenId, ieId)) {
					handle(message, session);
				}
			}
		}
	}

	private Integer sendServiceWorkerEnable(Session session) {
		Integer messageId = MessageDispatcher.getMessageIdForMessageMethod(session.getId(), "ServiceWorker.enable");
		EmptyResponse response = new EmptyResponse(messageId);
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
		return messageId;
	}

	private Integer sendCanEmulateNetwork(Session session) {
		Integer messageId = MessageDispatcher.getMessageIdForMessageMethod(session.getId(),
				"Network.canEmulateNetworkConditions");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", false);
		ResultResponse response = new ResultResponse(messageId, result);
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
		return messageId;
	}

	private Integer sendInspectorEnable(Session session) {
		Integer messageId = MessageDispatcher.getMessageIdForMessageMethod(session.getId(), "Inspector.enable");
		EmptyResponse response = new EmptyResponse(messageId);
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
		return messageId;
	}

	private boolean shouldProcessMessage(String message, Integer sweId, Integer cenId, Integer ieId) {
		Integer messageId = org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils.getMessageId(message);
		return !messageId.equals(sweId) && !messageId.equals(cenId) && !messageId.equals(ieId);
	}

	private void handle(String message, Session session) throws IOException {
		MessageHandler handler = MessageHandlerFactory.getHandler(message);
		handler.handle(message, session);
	}

	private boolean receivedInspectorEnable(Session session) {
		return MessageDispatcher.sessionHistoryContains(session.getId(), "Inspector.enable");
	}
}
