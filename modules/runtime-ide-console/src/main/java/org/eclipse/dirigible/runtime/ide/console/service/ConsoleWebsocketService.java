package org.eclipse.dirigible.runtime.ide.console.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@ServerEndpoint("/websockets/v3/ide/console")
public class ConsoleWebsocketService {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleWebsocketService.class);

	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		logger.info("[ws:console] onOpen: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.trace("[ws:console] onMessage: " + message);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:console] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:console] " + throwable.getMessage(), throwable);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("[ws:console] Session %s closed because of %s", session.getId(), closeReason));
		OPEN_SESSIONS.remove(session.getId());
	}

	public static void distribute(ConsoleLogRecord record) {
		for (Session session : OPEN_SESSIONS.values()) {
			synchronized (session) {
				session.getAsyncRemote().sendText(GsonHelper.GSON.toJson(record));
			}
		}
	}

}
