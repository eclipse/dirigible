package org.eclipse.dirigible.runtime.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

/**
 * WebSocket OSGi Proxy for Scripting Services support
 * It
 */
public class WebSocketServiceBridgeServletInternal {

	private static final Logger logger = Logger.getLogger(WebSocketServiceBridgeServletInternal.class);

	private static final String PARAM_SESSION = "wsSession";
	private static final String PARAM_SESSIONS = "wsSessions";

	public static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		logger.debug("[ws:exec] onOpen: " + session.getId());
	}

	public void onMessage(String message, Session session, String type) {
		logger.debug("[ws:exec] onMessage: " + message + ", type: " + type);

		WebSocketRequest webSocketRequest = null;
		try {
			webSocketRequest = WebSocketRequestParser.parseRequest(message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			try {
				session.getBasicRemote().sendText("Wrong format of the request");
			} catch (IOException e1) {
				logger.error(e.getMessage(), e);
			}
			return;
		}

		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		executionContext.put(PARAM_SESSION, session);
		executionContext.put(PARAM_SESSIONS, openSessions);
		executionContext.putAll(webSocketRequest.getParams());
		executeByEngineType(webSocketRequest.getModule(), executionContext, type);
	}

	private static void executeByEngineType(String module, Map<Object, Object> executionContext, String serviceType) {
		try {
			Set<String> types = EngineUtils.getAliases();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(serviceType)) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutorByAlias(type, null);
					scriptExecutor.executeServiceModule(null, null, module, executionContext);
					break;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void onError(Session session, String error) {
		logger.debug("[ws:exec] onError: " + error);
	}

	public void onClose(Session session) {
		openSessions.remove(session.getId());
		logger.debug("[ws:exec] onClose: Session " + session.getId() + " has ended");
	}

}
