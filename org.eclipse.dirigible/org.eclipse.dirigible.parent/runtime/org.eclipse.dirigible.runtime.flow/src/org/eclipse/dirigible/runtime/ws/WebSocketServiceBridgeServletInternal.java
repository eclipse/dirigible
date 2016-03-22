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

public class WebSocketServiceBridgeServletInternal {

	private static final Logger logger = Logger.getLogger(WebSocketServiceBridgeServletInternal.class);

	private static final String PARAM_SESSION = "wsSession";

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	public void onOpen(Session session) throws IOException {
		openSessions.put(session.getId(), session);
		logger.debug("[Internal] onOpen: " + session.getId());
	}

	public void onMessage(String message, Session session, String type) {
		logger.debug("[Internal] onMessage: " + message + ", type: " + type);

		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		executionContext.put(PARAM_SESSION, session);
		executeByEngineType(message, executionContext, type);
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
		logger.debug("[Internal] onError: " + error);
	}

	public void onClose(Session session) {
		openSessions.remove(session.getId());
		logger.debug("[Internal] onClose: Session " + session.getId() + " has ended");
	}

}
