package org.eclipse.dirigible.engine.js.debug;

import java.io.IOException;
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
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@ServerEndpoint("/websockets/v3/ide/debug")
public class JavascriptDebugWebsocketService {
	
	private static final Logger logger = LoggerFactory.getLogger(JavascriptDebugWebsocketService.class);

	private static Map<String, Session> OPEN_SESSIONS = new ConcurrentHashMap<String, Session>();

	/**
	 * On open callback.
	 *
	 * @param session
	 *            the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		OPEN_SESSIONS.put(session.getId(), session);
		// TODO consider separation by user
		Configuration.set(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_DEBUG_ENABLED, "true");
		logger.info("[ws:debug] onOpen: " + session.getId());
	}

	/**
	 * On message callback.
	 *
	 * @param message
	 *            the message
	 * @param session
	 *            the session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.trace("[ws:debug] onMessage: " + message);
	}

	/**
	 * On error callback.
	 *
	 * @param session
	 *            the session
	 * @param throwable
	 *            the throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info(String.format("[ws:debug] Session %s error %s", session.getId(), throwable.getMessage()));
		logger.error("[ws:debug] " + throwable.getMessage(), throwable);
	}

	/**
	 * On close callback.
	 *
	 * @param session
	 *            the session
	 * @param closeReason
	 *            the close reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("[ws:console] Session %s closed because of %s", session.getId(), closeReason));
		OPEN_SESSIONS.remove(session.getId());
		if (OPEN_SESSIONS.isEmpty()) {
			Configuration.set(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_DEBUG_ENABLED, "false");
		}
	}

	/**
	 * Distribute message to all the listeners.
	 *
	 * @param event
	 *            the event
	 */
	public static void distribute(DebugEvent event) {
		for (Session session : OPEN_SESSIONS.values()) {
			synchronized (session) {
				try {
					if (session.isOpen()) {
						session.getBasicRemote().sendText(GsonHelper.GSON.toJson(event));
					}
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}


}
