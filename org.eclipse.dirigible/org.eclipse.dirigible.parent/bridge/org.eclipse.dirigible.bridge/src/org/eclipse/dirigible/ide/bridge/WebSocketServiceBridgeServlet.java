package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/service/{type}")
public class WebSocketServiceBridgeServlet {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketServiceBridgeServlet.class);

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		callInternal("onOpen", session, null, null);
	}

	protected void callInternal(String methodName, Session session, String message, String type) {

		logger.debug("Getting internal pair...");
		Object debugInternal = System.getProperties().get("websocket_service_channel_internal");
		logger.debug("Getting internal pair passed: " + (debugInternal != null));

		if (debugInternal == null) {
			logger.error("Internal WebSocket Exec peer is null.");
			return;
		}

		try {
			Method method = null;
			if (message == null) {
				method = debugInternal.getClass().getMethod(methodName, Session.class);
				method.invoke(debugInternal, session);
			} else {
				method = debugInternal.getClass().getMethod(methodName, String.class, Session.class, String.class);
				method.invoke(debugInternal, message, session, type);
			}
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(), e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("type") String type) {
		callInternal("onMessage", session, message, type);
	}

	@OnError
	public void onError(Session session, Throwable t) {
		callInternal("onError", session, t.getMessage(), null);
		logger.error(t.getMessage(), t);
	}

	@OnClose
	public void onClose(Session session) {
		openSessions.remove(session.getId());
		callInternal("onClose", session, null, null);
	}

}
