package org.eclipse.dirigible.runtime.js.debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.ext.debug.DebugModelFacade;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.chrome.debugger.handlers.OnExceptionHandler;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;

public class WebSocketDebugBridgeServletInternal {

	private static final Logger logger = Logger.getLogger(WebSocketDebugBridgeServletInternal.class);

	private static Map<String, List<Session>> openUserSessions = new ConcurrentHashMap<String, List<Session>>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		String userId = RequestUtils.getUser(session);
		List<Session> userSessions = openUserSessions.get(userId);
		if (userSessions == null) {
			userSessions = new ArrayList<Session>();
		}
		userSessions.add(session);
		openUserSessions.put(userId, userSessions);
		DebugModelFacade.createDebugModel(RequestUtils.getUser(session), new WebSocketDebugController(userId));
		logger.debug("[Internal] onOpen: " + userId);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		logger.debug("[Internal] onMessage: " + message);
		MessageDispatcher.receiveFrom(message, session);
		try {
			new DebugMessageProcessor().processMessage(message, session);
		} catch (IOException e) {
			handleError(session, e);
		}
	}

	@OnError
	public void onError(Session session, String error) {
		logger.error("[Internal] onError: " + error);
		handleError(session, new Throwable(error));
	}

	@OnClose
	public void onClose(Session session) {
		String userId = RequestUtils.getUser(session);
		List<Session> userSessions = openUserSessions.get(userId);
		if (userSessions == null) {
			logger.error("[Internal] onClose: Could not find the given session for currently active user!");
			return;
		}
		Iterator<Session> iterator = userSessions.iterator();
		while (iterator.hasNext()) {
			Session nextSession = iterator.next();
			if (nextSession.getId().equalsIgnoreCase(session.getId())) {
				iterator.remove();
			}
		}
		if (userSessions.isEmpty()) {
			openUserSessions.remove(userId);
		}
		logger.debug("[Internal] onClose: Session " + userId + " has ended");
	}

	private void handleError(Session session, Throwable e) {
		logger.error(e.getMessage(), e);
		try {
			new OnExceptionHandler().handle(e.getMessage(), session);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	public void closeAll() {
		for (List<Session> openSessions : openUserSessions.values()) {
			for (Session session : openSessions) {
				try {
					synchronized (session) {
						session.close();
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
}
