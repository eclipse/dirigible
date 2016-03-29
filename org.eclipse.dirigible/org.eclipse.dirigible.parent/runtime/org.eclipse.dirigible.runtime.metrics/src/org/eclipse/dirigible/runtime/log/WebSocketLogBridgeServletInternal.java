package org.eclipse.dirigible.runtime.log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.logging.ILogListener;
import org.eclipse.dirigible.repository.logging.Logger;

public class WebSocketLogBridgeServletInternal implements ILogListener {

	private static final Logger logger = Logger.getLogger(WebSocketLogBridgeServletInternal.class);

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		session.getBasicRemote().sendText("[Internal] onOpen: " + session.getId());
		logger.debug("[Internal] onOpen: " + session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.debug("[Internal] onMessage: " + message);
	}

	@OnError
	public void onError(Session session, String error) {
		logger.debug("[Internal] onError: " + error);
	}

	@OnClose
	public void onClose(Session session) {
		openSessions.remove(session.getId());
		logger.debug("[Internal] onClose: Session " + session.getId() + " has ended");
	}

	public static void sendText(String sessionId, String message) {
		try {
			if (sessionId == null) {
				for (Object element : openSessions.values()) {
					Session session = (Session) element;
					session.getBasicRemote().sendText(message);
				}
			} else {
				openSessions.get(sessionId).getBasicRemote().sendText(message);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void log(String level, String message) {
		for (Session session : openSessions.values()) {
			try {
				synchronized (session) {
					session.getBasicRemote().sendText(String.format("[%s] %s", level, message));
				}
			} catch (Throwable e) {
				// do not log it with the Logger
				e.printStackTrace();
			}
		}
	}

	public void closeAll() {
		for (Session session : openSessions.values()) {
			try {
				synchronized (session) {
					session.close();
				}
			} catch (Throwable e) {
				// do not log it with the Logger
				e.printStackTrace();
			}
		}
	}

}
